package uz.sanjar.androidexam31.ui.screen.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import uz.sanjar.androidexam31.MyShape
import uz.sanjar.androidexam31.R

/**   Created by Sanjar Karimov 4:21 PM 12/21/2024   */

class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val drawPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val path: Path = Path()
    private val erasePath: Path = Path()

    private val erasePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private var isErasing = false

    // Eraser uchun yaxshi usul ekan !!!
    private var drawingBitmap = createDrawingBitmap()
    private var drawingCanvas = Canvas(drawingBitmap)

    // History for prev/next
    private val history = mutableListOf<Bitmap>()
    private var currentHistoryIndex = -1
    var isPrev = false
    var isNext = false
    var onHistoryStateChange: ((isPrev: Boolean, isNext: Boolean) -> Unit)? = null

    // Track selected shape and color
    var selectedShape: MyShape = MyShape.NONE
        set(value) {
            selectedColor = color
            drawPaint.color = color
            field = value
            invalidate()
        }
    private var selectedColor: Int = Color.BLACK


    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.PaintView, defStyleAttr, 0)

        drawPaint.color = attributes.getColor(R.styleable.PaintView_color, Color.BLUE)// Color.BLUE
        drawPaint.strokeWidth = attributes.getInt(R.styleable.PaintView_stroke, 1).toFloat()
        drawPaint.style = Paint.Style.STROKE

        erasePaint.strokeWidth = drawPaint.strokeWidth

        attributes.recycle()
        saveState()
    }

    private fun createDrawingBitmap(): Bitmap {
        return Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
    }

    fun clear() {
        path.reset()
        erasePath.reset()
        drawingBitmap.eraseColor(Color.TRANSPARENT)
        invalidate()
    }

    var stroke: Float
        get() = drawPaint.strokeWidth
        set(value) {
            drawPaint.strokeWidth = value
            erasePaint.strokeWidth = value
            invalidate()
        }

    var color: Int
        get() = drawPaint.color
        set(value) {
            drawPaint.color = value
            invalidate()
        }

    var eraser: Boolean
        get() = isErasing
        set(value) {
            isErasing = value
            invalidate()
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isErasing)
                    erasePath.moveTo(x, y)
                else
                    path.moveTo(x, y)
            }


            MotionEvent.ACTION_MOVE -> {
                if (isErasing) {
                    erasePath.lineTo(x, y)
                    drawingCanvas.drawPath(erasePath, erasePaint) // Apply erase immediately
                    erasePath.reset()
                    erasePath.moveTo(x, y) // Reset path for smoother erasing
                } else if (selectedShape == MyShape.NONE) {
                    // If no shape is selected, draw free-hand line
//                    path.moveTo(x, y)
                    path.lineTo(x, y)
                    drawingCanvas.drawPath(path, drawPaint) // Apply draw immediately
                    path.reset()
                    path.moveTo(x, y)
                } else {
                    // Start drawing the selected shape
                    when (selectedShape) {
                        MyShape.TRIANGLE -> drawTriangle(x, y)
                        MyShape.CIRCLE -> drawCircle(x, y)
                        MyShape.RECTANGLE -> drawRectangle(x, y)
                        else -> drawTriangle(x, y)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                saveState()
            }

            else -> return false
        }

        invalidate()
        return true
    }

    private fun drawTriangle(x: Float, y: Float) {
        val sideLength = 100f // You can adjust this value
        val path = Path().apply {
            moveTo(x, y)
            lineTo(x + sideLength, y)
            lineTo(x + sideLength / 2, y - sideLength)
            close()
        }
        drawingCanvas.drawPath(path, drawPaint)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(drawingBitmap, 0f, 0f, null)

        if (isErasing)
            canvas.drawPath(path, erasePaint)
        else
            canvas.drawPath(path, drawPaint)
    }

    private fun drawCircle(x: Float, y: Float) {
        val radius = 50f // You can adjust this value
        val path = Path().apply {
            addCircle(x, y, radius, Path.Direction.CW)
        }
        drawingCanvas.drawPath(path, drawPaint)
    }

    private fun drawRectangle(x: Float, y: Float) {
        val width = 100f // You can adjust this value
        val height = 50f // You can adjust this value
        val rect = android.graphics.RectF(x, y, x + width, y + height)
        drawingCanvas.drawRect(rect, drawPaint)
    }

    /*
        fun setSelectedShape(shape: MyShape) {
            selectedShape = shape
            invalidate() // Redraw to reflect the selected shape
        }
    */


    fun prev() {
        if (currentHistoryIndex > 0) {
            currentHistoryIndex--
            isPrev = true
            restoreState()
        }
        updateHistoryState()
    }

    fun next() {
        if (currentHistoryIndex < history.size - 1) {
            currentHistoryIndex++
            restoreState()
        }
        updateHistoryState()
    }

    private fun restoreState() {
        if (history[currentHistoryIndex].config == null) return
        drawingBitmap = history[currentHistoryIndex].copy(history[currentHistoryIndex].config!!, true)
        drawingCanvas = Canvas(drawingBitmap)
        invalidate()
    }

    private fun saveState() {
        // Trim history if we are in the middle (after undoing)
        if (currentHistoryIndex < history.size - 1) {
            history.subList(currentHistoryIndex + 1, history.size).clear()
        }

        // Save a copy of the current bitmap
        history.add(drawingBitmap.copy(drawingBitmap.config!!, true))
        currentHistoryIndex++
        updateHistoryState()
    }

    private fun updateHistoryState() {
        isPrev = currentHistoryIndex > 0
        isNext = currentHistoryIndex < history.size - 1
        onHistoryStateChange?.invoke(isPrev, isNext)
    }

    fun getDrawingBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}


/*            MotionEvent.ACTION_MOVE -> {
                if (isErasing)
                    erasePath.lineTo(x, y)
                else
                    path.lineTo(x, y)
            }*/

/*MotionEvent.ACTION_UP -> {
                if (isErasing) {
                    drawingCanvas.drawPath(erasePath, erasePaint)
                    erasePath.reset() // Clear the temporary path after applying it
                } else {
                    drawingCanvas.drawPath(path, drawPaint)
                    path.reset() // Clear the temporary path after applying it
                }
            }*/

/*else if (selectedShape != null) {
                    // Start drawing the selected shape
                    when (selectedShape) {
                        MyShape.TRIANGLE -> drawTriangle(x, y)
                        MyShape.CIRCLE -> drawCircle(x, y)
                        MyShape.RECTANGLE -> drawRectangle(x, y)
                        else -> drawTriangle(x, y)
                    }
                } else {
                    path.lineTo(x, y)
                    drawingCanvas.drawPath(path, drawPaint) // Apply draw immediately
                    path.reset()
                    path.moveTo(x, y) // Reset path for smoother drawing
                }*/