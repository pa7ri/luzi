package com.master.iot.luzi.domain.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper.Callback
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R


open class SwipeCallback(context: Context) : Callback() {

    private var swipeBack: Boolean = false
    private var mClearPaint: Paint
    private var iconDrawable: Drawable
    private var mBackground: ColorDrawable = ColorDrawable()
    private var backgroundColor = 0
    private var iconWidth = 0
    private var iconHeight = 0

    init {
        backgroundColor = ContextCompat.getColor(context, R.color.blue_charcoal_700)
        mClearPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_alert)!!
        iconWidth = iconDrawable.intrinsicWidth
        iconHeight = iconDrawable.intrinsicHeight

    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, LEFT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        mBackground.color = backgroundColor
        mBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(c)
        val deleteIconTop: Int = itemView.top + (itemHeight - iconHeight) / 2
        val deleteIconMargin: Int = (itemHeight - iconHeight) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - iconWidth
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom: Int = deleteIconTop + iconHeight
        iconDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        iconDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }


    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }
}