package com.example.moblpr

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        var plateReturn: String? = null

        for (capturedText: String in capturedTexts) {
            var plate = capturedText.replace("[-\\s]".toRegex(), "")

            if (plate.length == 7 && plate.contains("\\d".toRegex())) {
                val plateChars = plate.toCharArray()

                // se contains numero nas 3 primeiras posições da placa, tá errado, só pode letra
                if (plate.substring(0, 3).contains("\\d".toRegex())) {
                    for (i in 0..2) {
                        if (plateChars[i] == '0') {
                            plateChars[i] = 'O'
                        }
                        if (plateChars[i] == '1') {
                            plateChars[i] = 'I'
                        }
                    }
                }

                // se na quarta posição da placa for uma letra, ta errado, só pode numero
                if (plate.substring(3, 4).contains("[A-Z]".toRegex())) {
                    if (plateChars[3] == 'O' || plateChars[3] == 'Q') {
                        plateChars[3] = '0'
                    } else if (plateChars[3] == 'I') {
                        plateChars[3] = '1'
                    }
                }

                // se contains letra nas 2 últimas posições da placa, tá errado, só pode numero
                if (plate.substring(5).contains("\\D".toRegex())) {
                    for (i in 5..6) {
                        if (plateChars[i] == 'O' || plateChars[i] == 'Q') {
                            plateChars[i] = '0'
                        } else if (plateChars[i] == 'I') {
                            plateChars[i] = '1'
                        }
                    }
                }

                plate = String(plateChars)

                if (plate.matches("^([A-Z]{3})(\\d)(\\d|[A-Z])(\\d{2})\$".toRegex())) {
                    plateReturn = plate
                    break
                }
            }
        }

        return plateReturn
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}