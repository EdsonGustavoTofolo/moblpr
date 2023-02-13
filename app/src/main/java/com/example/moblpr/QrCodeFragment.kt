package com.example.moblpr

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.moblpr.databinding.FragmentQrCodeBinding
import com.example.moblpr.models.Vehicle
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeFragment : Fragment() {

    private var _binding: FragmentQrCodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val imageQrCodeViewModel: ImageQrCodeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        mainActivity.progressBarShow()

        val vehicle = arguments?.getSerializable("vehicle") as Vehicle

        val qrCodeImage = generateQrCode(vehicle.toString())

        imageQrCodeViewModel.selectItem(qrCodeImage)

        binding.qrCodeIv.setImageBitmap(qrCodeImage)

        mainActivity.showShareQrCodeOption()

        mainActivity.progressBarHide()
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        mainActivity.checkStateButtons()
    }

    private fun generateQrCode(data: String): Bitmap {
        val mainActivity = activity as MainActivity

        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                512, 512
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            mainActivity.progressBarHide()
            Toast.makeText(mainActivity, "Falha ao gerar qrcode: ${e.message}", Toast.LENGTH_SHORT).show()
            throw e
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}