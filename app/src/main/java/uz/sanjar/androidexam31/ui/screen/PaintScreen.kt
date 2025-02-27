package uz.sanjar.androidexam31.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.permissionx.guolindev.PermissionX
import uz.sanjar.androidexam31.MyShape
import uz.sanjar.androidexam31.R
import uz.sanjar.androidexam31.databinding.ScreenPaintBinding

class PaintScreen : Fragment(R.layout.screen_paint) {
    private val binding: ScreenPaintBinding by viewBinding(ScreenPaintBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val paintView = binding.paintView
        binding.apply {
            thickLevel1.setOnClickListener {
                paintView.stroke = 10f
            }
            thickLevel2.setOnClickListener {
                paintView.stroke = 20f
            }
            thickLevel3.setOnClickListener {
                paintView.stroke = 30f
            }
            clearButton.setOnClickListener { paintView.clear() }
            eraserButton.setOnClickListener {
                if (eraserButton.isSelected) {
                    paintView.eraser = false
                    eraserButton.isSelected = false
                } else {
                    paintView.eraser = true
                    eraserButton.isSelected = true
                }
            }
            prevButton.setOnClickListener { paintView.prev() }
            nextButton.setOnClickListener { paintView.next() }

            paintView.onHistoryStateChange = { isPrev, isNext ->
                prevButton.isEnabled = isPrev
                nextButton.isEnabled = isNext
            }
            prevButton.isEnabled = paintView.isPrev
            nextButton.isEnabled = paintView.isNext

            saveButton.setOnClickListener {
                saveImageToGallery(paintView.getDrawingBitmap())
            }

            triangle.setOnClickListener {
                // Set all shapes to unselected
                resetShapeSelection()
                // Select triangle shape
                paintView.selectedShape = MyShape.TRIANGLE
                triangle.isSelected = true
            }
            circle.setOnClickListener {
                // Set all shapes to unselected
                resetShapeSelection()
                // Select circle shape
                paintView.selectedShape = MyShape.CIRCLE
                circle.isSelected = true
            }
            rectangle.setOnClickListener {
                // Set all shapes to unselected
                resetShapeSelection()
                // Select rectangle shape
                paintView.selectedShape = MyShape.RECTANGLE
                rectangle.isSelected = true
            }

            colorRed.setOnClickListener {
                // Set all colors to unselected
                resetColorSelection()
                // Select red color
                paintView.color = Color.RED
                colorRed.isSelected = true
            }
            colorBlack.setOnClickListener {
                // Set all colors to unselected
                resetColorSelection()
                // Select black color
                paintView.color = Color.BLACK
                colorBlack.isSelected = true
            }
            colorGreen.setOnClickListener {
                // Set all colors to unselected
                resetColorSelection()
                // Select green color
                paintView.color = Color.GREEN
                colorGreen.isSelected = true
            }
            binding.pencil.setOnClickListener {
                resetShapeSelection()
                paintView.selectedShape = MyShape.NONE
                pencil.isSelected = true
            }
        }
    }

    private fun resetShapeSelection() {
        binding.triangle.isSelected = false
        binding.circle.isSelected = false
        binding.rectangle.isSelected = false
        binding.pencil.isSelected = false
    }

    private fun resetColorSelection() {
        binding.colorRed.isSelected = false
        binding.colorBlack.isSelected = false
        binding.colorGreen.isSelected = false
    }

    @SuppressLint("NewApi")
    private fun saveImageToGallery(bitmap: Bitmap) {
        // Request permission using PermissionX
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted)
                    saveImage(bitmap)

            }
    }

    @SuppressLint("NewApi")
    private fun saveImage(bitmap: Bitmap) {
        val contentResolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "PaintDrawing_${System.currentTimeMillis()}.png"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
        }

        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
                Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}