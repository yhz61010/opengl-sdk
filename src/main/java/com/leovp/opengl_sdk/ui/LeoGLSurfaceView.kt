package com.leovp.opengl_sdk.ui

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import com.leovp.lib_common_android.exts.screenAvailableHeight
import com.leovp.lib_common_android.exts.screenWidth
import com.leovp.lib_common_android.utils.TouchHelper
import com.leovp.log_sdk.LogContext
import com.leovp.opengl_sdk.BaseRenderer
import com.leovp.opengl_sdk.GLRenderer

// https://download.csdn.net/download/lkl22/11065372?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-download-2%7Edefault%7EBlogCommendFromBaidu%7ERate-3.pc_relevant_paycolumn_v3&depth_1-utm_source=distribute.pc_relevant.none-task-download-2%7Edefault%7EBlogCommendFromBaidu%7ERate-3.pc_relevant_paycolumn_v3&utm_relevant_index=6
class LeoGLSurfaceView(context: Context,
    attributeSet: AttributeSet? = null) : GLSurfaceView(context, attributeSet) {
    companion object {
        private const val TAG = "LGLSV"
    }

    fun setKeepRatio(keepRatio: Boolean) {
        renderer.keepRatio = keepRatio
    }

    // ----------------------------------------
    var touchHelper: TouchHelper? = null

    fun setTouchListener(listener: TouchHelper.TouchListener) {
        touchHelper = TouchHelper(listener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchHelper?.onTouchEvent(event) ?: performClick()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun updateDimension(width: Int, height: Int) {
        LogContext.log.w(TAG, "Adjust GLSurfaceView dimension to $width x $height")
        val params: ViewGroup.LayoutParams = layoutParams
        // Changes the height and width to the specified *pixels*
        params.width = width
        params.height = height
        layoutParams = params
    }

    // ----------------------------------------

    private val renderer: GLRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // RENDERMODE_CONTINUOUSLY: The renderer is called continuously to re-render the scene.
        // Render the view only when there is a change in the drawing data.
        // That means if only requestRender() is called, it will do render.
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /**
     * ??????????????? YUV ???????????????
     * @param videoWidth ??????
     * @param videoHeight ??????
     */
    fun setVideoDimension(videoWidth: Int, videoHeight: Int) {
        LogContext.log.d(TAG, "setVideoDimension: $videoWidth*$videoHeight")
        renderer.setVideoDimension(videoWidth,
            videoHeight,
            context.screenWidth,
            context.screenAvailableHeight)
    }

    /**
     * ???????????? YUV ????????????
     * @param yuvData YUV ???????????????
     * @param type YUV ??????????????? 0 -> I420  1 -> NV12  2 -> NV21
     */
    fun render(yuvData: ByteArray?, type: BaseRenderer.Yuv420Type = BaseRenderer.Yuv420Type.I420) {
        if (yuvData == null) {
            return
        }
        renderer.feedData(yuvData, type)
        // ?????????????????? YUV ??????
        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ?????????????????? Renderer ????????? onDrawFrame ?????????
        requestRender()
    }
}