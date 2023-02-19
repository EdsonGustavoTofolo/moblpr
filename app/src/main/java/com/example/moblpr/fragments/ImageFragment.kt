package com.example.moblpr.fragments

import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.moblpr.viewmodels.ImageUriViewModel
import com.example.moblpr.MainActivity
import com.example.moblpr.recognizer.PlateRecognizer
import com.example.moblpr.R
import com.example.moblpr.clients.VeiculoClient
import com.example.moblpr.databinding.DialogConfirmationLicensePlateBinding
import com.example.moblpr.databinding.FragmentImageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val imageViewModel: ImageUriViewModel by activityViewModels()

    private lateinit var dialogConfirmation: AlertDialog
    private lateinit var licensePlateConfirmationDialogBinding: DialogConfirmationLicensePlateBinding
    private lateinit var textRecognizer: TextRecognizer
    private var hasImage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.also {
            licensePlateConfirmationDialogBinding =
                DialogConfirmationLicensePlateBinding.inflate(LayoutInflater.from(it))

            licensePlateConfirmationDialogBinding.seventhPos.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialogConfirmation.also { dialog ->
                        confirmPlate(dialog)
                    }

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            dialogConfirmation = MaterialAlertDialogBuilder(it)
                .setView(licensePlateConfirmationDialogBinding.root)
                .setTitle("Confirmação da Placa")
                .setMessage("Placa Reconhecida")
                .setPositiveButton("Confirmar") { dialog, _ ->
                    confirmPlate(dialog)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        }

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        imageViewModel.selectedItem.observe(viewLifecycleOwner) { uri ->
            if (!hasImage) {
                hasImage = true
                binding.carIv.visibility = View.GONE
                binding.cropIv.visibility = View.VISIBLE
            }
            binding.cropIv.setImageUriAsync(uri)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.carIv.visibility = if (hasImage) View.GONE else View.VISIBLE
        binding.cropIv.visibility = if (hasImage) View.VISIBLE else View.GONE

        val mainActivity = activity as MainActivity
        mainActivity.visibilityButtonsImageFrag(if(hasImage) View.VISIBLE else View.GONE)
    }

    fun scan() {
        val mainActivity = activity as MainActivity

        val croppedImage: Bitmap? = binding.cropIv.getCroppedImage()

        croppedImage?.also { img ->
            textRecognizer.process(img, 0)
                .addOnSuccessListener { value ->
                    val plate = PlateRecognizer.execute(value.text)
                    if (plate.isNullOrBlank()) {
                        mainActivity.progressBarHide()
                        mainActivity.snackBarShow("Não foi possível reconhecer a placa. Tente novamente.")
                    } else {
                        showDialogConfirmation(plate)
                    }
                }
                .addOnFailureListener { e ->
                    mainActivity.progressBarHide()
                    mainActivity.snackBar("Falha ao scannear a imagem: ${e.message}")
                        .setAction("Scan") {
                            mainActivity.progressBarShow("Fazendo reconhecimento da placa...")
                            scan()
                        }.show()
                }
        }
    }

    private fun showDialogConfirmation(plate: String) {
        val mainActivity = activity as MainActivity
        mainActivity.progressBarHide()

        context?.also {
            licensePlateConfirmationDialogBinding.firstPos.setText(plate[0].toString())
            licensePlateConfirmationDialogBinding.secondPos.setText(plate[1].toString())
            licensePlateConfirmationDialogBinding.thirdPos.setText(plate[2].toString())
            licensePlateConfirmationDialogBinding.fourthPos.setText(plate[3].toString())
            licensePlateConfirmationDialogBinding.fifthPos.setText(plate[4].toString())
            licensePlateConfirmationDialogBinding.sixthPos.setText(plate[5].toString())
            licensePlateConfirmationDialogBinding.seventhPos.setText(plate[6].toString())

            dialogConfirmation.show()
        }
    }

    private fun confirmPlate(dialog: DialogInterface) {
        val confirmedPlate =
            licensePlateConfirmationDialogBinding.firstPos.text.toString() +
                    licensePlateConfirmationDialogBinding.secondPos.text.toString() +
                    licensePlateConfirmationDialogBinding.thirdPos.text.toString() +
                    licensePlateConfirmationDialogBinding.fourthPos.text.toString() +
                    licensePlateConfirmationDialogBinding.fifthPos.text.toString() +
                    licensePlateConfirmationDialogBinding.sixthPos.text.toString() +
                    licensePlateConfirmationDialogBinding.seventhPos.text.toString()

        dialog.dismiss()

        val mainActivity = activity as MainActivity
        mainActivity.progressBarShow("Buscando dados do veículo...")

        findCarsInfoByPlate(confirmedPlate)
    }

    private fun findCarsInfoByPlate(plate: String) {
        val mainActivity = activity as MainActivity

        VeiculoClient.findByPlaca(plate,
            { vehicle ->
                val bundle = Bundle()
                bundle.putSerializable("vehicle", vehicle)

                findNavController().navigate(R.id.action_FirstFragment_to_carInfoFragment, bundle)

                mainActivity.showGenerateQrCodeOption()
            },
            { error ->
                mainActivity.progressBarHide()
                mainActivity.snackBar(error).setAction("Scan") {
                    mainActivity.progressBarShow("Fazendo reconhecimento da placa...")
                    scan()
                }.show()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}