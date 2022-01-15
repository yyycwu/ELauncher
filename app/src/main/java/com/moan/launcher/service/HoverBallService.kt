package com.moan.launcher.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.getSystemService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.moan.launcher.MainActivity
import com.moan.launcher.R
import com.moan.launcher.event.MessageEvent
import com.moan.launcher.utils.ActionKey
import com.moan.launcher.utils.FileUtils
import com.moan.launcher.utils.SpUtil
import com.moan.launcher.widget.HoverBallView
import java.io.File
import kotlin.concurrent.thread

/**
 *
 * 启动悬浮窗界面
 */
class HoverBallService : Service() {
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    lateinit var mImageReader: ImageReader
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenDensity = 0

    private val wm by lazy { this.getSystemService<WindowManager>()!! }

    private val hoverBallView: HoverBallView by lazy { HoverBallView(baseContext) }

    var showBall = false

    lateinit var mediaProjectionManager: MediaProjectionManager


    companion object {
        var resultData: Intent? = null
    }


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        createFloatView()

        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        createImageReader()

        if (resultData == null
            && !SpUtil.getBoolean(this, ActionKey.SHOW_SCREENSHOT_REQUEST, true)) {
            val spAction = arrayListOf(
                ActionKey.HOVER_BALL_CLICK,
                ActionKey.HOVER_BALL_DOUBLE_CLICK,
                ActionKey.HOVER_BALL_LONG_CLICK,
                ActionKey.HOVER_BALL_UP,
                ActionKey.HOVER_BALL_DOWN,
                ActionKey.HOVER_BALL_LEFT,
                ActionKey.HOVER_BALL_RIGHT
            )
            //检查截图功能
            for (actionName in spAction) {
                if (SpUtil.getInt(this, actionName, 0) == 8) {
                    sendBroadcast(Intent(ActionKey.ACTION_SYSTEM_SCREENSHOT))
                    val mHandler = Handler()
                    mHandler.postDelayed({
                        //开启桌面
                        startActivity(Intent(this, MainActivity::class.java))
                    }, 5000)
                    break
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when (event?.action) {
            ActionKey.ACTION_HOVER_BALL -> {
                if (showBall) {
                    wm.removeView(hoverBallView)
                    showBall = false
                } else {
                    createFloatView()
                }
            }
            ActionKey.HOVER_BALL_ALPHA -> {
                val alpha = SpUtil.getInt(this, ActionKey.HOVER_BALL_ALPHA, 100) / 100.0f

                hoverBallView.alpha = alpha
            }
            ActionKey.HOVER_BALL_ICON_INDEX -> {
                setBallBackground()
            }

            ActionKey.HOVER_BALL_SIZE -> {
                wm.removeView(hoverBallView)
                createFloatView()
            }
            ActionKey.ACTION_SYSTEM_SCREENSHOT -> {
                startScreenShot()
            }
        }
    }

    /**
     * 设置悬浮球背景
     */
    private fun setBallBackground() {
        val ballIconIndex = SpUtil.getInt(this, ActionKey.HOVER_BALL_ICON_INDEX, 0)

        when (ballIconIndex) {
            0 -> {
                hoverBallView.background = resources.getDrawable(R.mipmap.icon_circle)
            }
            1 -> {
                hoverBallView.background = resources.getDrawable(R.mipmap.icon_flower)
            }
            2 -> {
                val userIconPath =
                    SpUtil.getString(this, ActionKey.HOVER_BALL_ICON_PATH, "")
                val file = File(userIconPath ?: "")
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(userIconPath)
                    hoverBallView.background = BitmapDrawable(bitmap)
                } else {
                    hoverBallView.background = resources.getDrawable(R.mipmap.icon_circle)
                }
            }
            else -> hoverBallView.background = resources.getDrawable(R.mipmap.icon_circle)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createFloatView() {
        val point = Point()
        wm.defaultDisplay.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y

        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels

        val smallWindowParams = WindowManager.LayoutParams()

        val ballSize = SpUtil.getInt(this, ActionKey.HOVER_BALL_SIZE, 50)

        smallWindowParams.type = 2038
        smallWindowParams.format = 1
        smallWindowParams.flags = 40
        smallWindowParams.gravity = 51
        smallWindowParams.width = ballSize
        smallWindowParams.height = ballSize


        val userX = SpUtil.getInt(this, ActionKey.HOVER_BALL_X, (screenWidth - ballSize - 10))
        val userY = SpUtil.getInt(this, ActionKey.HOVER_BALL_Y, (screenHeight / 2) - 50)

        smallWindowParams.x = userX
        smallWindowParams.y = userY

        wm.addView(hoverBallView, smallWindowParams)

        val alpha = SpUtil.getInt(this, ActionKey.HOVER_BALL_ALPHA, 100) / 100.0f
        showBall = true
        hoverBallView.alpha = alpha

        setBallBackground()
    }


    private fun startScreenShot() {
        hoverBallView.alpha = 0f //截图时隐藏圆点
        val mHandler = Handler()
        mHandler.postDelayed({ //start virtual
            startVirtual()
        }, 5)
        mHandler.postDelayed({ //capture the screen
            startCapture()
        }, 30)
    }

    private fun createImageReader() {
        mImageReader = ImageReader.newInstance(
            mScreenWidth,
            mScreenHeight,
            PixelFormat.RGBA_8888,
            1
        )
    }

    fun startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay()
        } else {
            setUpMediaProjection()
            virtualDisplay()
        }
    }

    fun setUpMediaProjection() {
        if (resultData == null) {
            sendBroadcast(Intent(ActionKey.ACTION_SYSTEM_SCREENSHOT))
        } else {
            mMediaProjection = mediaProjectionManager.getMediaProjection(
                Activity.RESULT_OK,
                resultData!!
            )
        }
    }


    private fun virtualDisplay() {
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            "screen-mirror",
            mScreenWidth,
            mScreenHeight,
            mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.surface,
            null,
            null
        )
    }

    private fun startCapture() {
        val image = mImageReader.acquireLatestImage()
        if (image == null) {
            if (resultData == null) {
                //等待开启权限
                val mHandler = Handler()
                mHandler.postDelayed({
                    //开启桌面
                    startActivity(Intent(this, MainActivity::class.java))
                    //等待结果
                    mHandler.postDelayed({
                        //检查结果
                        if (resultData != null) {
                            startScreenShot()
                        } else {
                            //截图失败
                            hoverBallView.alpha = SpUtil.getInt(this, ActionKey.HOVER_BALL_ALPHA, 100) / 100.0f
                        }
                    }, 1000)
                }, 5000)
            } else {
                startScreenShot()
            }
        } else {
            thread {

                val width = image.width
                val height = image.height
                val planes = image.planes
                val buffer = planes[0].buffer
                //每个像素的间距
                val pixelStride = planes[0].pixelStride
                //总的间距
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width
                var bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap!!.copyPixelsFromBuffer(buffer)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                image.close()

                val path = FileUtils.saveImage(bitmap)
                Log.e(">>>>>>>>", "截图地址" + path)
            }
            Toast.makeText(baseContext, "截图成功,请在ELauncher目录查看", Toast.LENGTH_SHORT).show()
            //截图后恢复原始值
            hoverBallView.alpha = SpUtil.getInt(this, ActionKey.HOVER_BALL_ALPHA, 100) / 100.0f

        }
    }

    private fun tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }

    private fun stopVirtual() {
        mVirtualDisplay?.release()
        mVirtualDisplay = null
    }

    override fun onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy()
        stopVirtual()
        tearDownMediaProjection()
        wm.removeView(hoverBallView)

        EventBus.getDefault().unregister(this);
    }


}