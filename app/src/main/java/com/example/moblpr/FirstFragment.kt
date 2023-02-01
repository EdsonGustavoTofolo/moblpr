package com.example.moblpr

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.moblpr.clients.VeiculoClient
import com.example.moblpr.databinding.FragmentFirstBinding
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val imageViewModel: ImageUriViewModel by activityViewModels()

    private lateinit var textRecognizer: TextRecognizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        imageViewModel.selectedItem.observe(viewLifecycleOwner) { uri ->
            binding.cropIv.setImageUriAsync(uri)
        }

//        val mainActivity = activity as MainActivity
//        mainActivity.hideShareQrCodeOption()

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    fun scan() {
        val mainActivity = activity as MainActivity

        val croppedImage: Bitmap? = binding.cropIv.getCroppedImage()

        val textTaskResult = croppedImage?.let { img ->
            textRecognizer.process(img, 0)
                .addOnSuccessListener { value ->
                    val plate = getPlate(value.text)
                    if (plate.isNullOrBlank()) {
                        mainActivity.progressBarHide()
                        mainActivity.snackBarShow("Não foi possível reconhecer a placa. Tente novamente.")
                    } else {
                        findCarsInfoByPlate(plate)
                    }
                }
                .addOnFailureListener { e ->
                    mainActivity.progressBarHide()
                    Toast.makeText(mainActivity, "Falha ao scannear a imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun findCarsInfoByPlate(plate: String) {
        val mainActivity = activity as MainActivity

        VeiculoClient.findByPlaca(plate,
            { vehicle ->
                mainActivity.progressBarHide()

                val bundle = Bundle()
                bundle.putSerializable("vehicle", vehicle)

                findNavController().navigate(R.id.action_FirstFragment_to_carInfoFragment, bundle)

                mainActivity.showGenerateQrCodeOption()
            },
            { error ->
                mainActivity.progressBarHide()
                Toast.makeText(mainActivity, "Falha ao buscar dados do carro: ${error.string()}", Toast.LENGTH_SHORT).show()
            })
    }

    private fun getPlate(text: String) : String? {
        val capturedTexts = text.split("\n")

        var plate: String? = null

        for (capturedText: String in capturedTexts) {
            if (capturedText.matches("^([A-Z]{3})(\\d|O|-|\\s)?([A-Z]{1}\\d{2}|\\d{4})$".toRegex())) {
                plate = capturedText.trim()
                val fourthChar = plate.substring(3, 4)
                if (fourthChar == "O") {
                    plate = plate.substring(0, 3) + "0" + plate.substring(4)
                } else if (fourthChar == "-" || fourthChar == " ") {
                    plate = plate.replace("(-|\\s)".toRegex(), "")
                }
                break
            }
        }

        return plate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}