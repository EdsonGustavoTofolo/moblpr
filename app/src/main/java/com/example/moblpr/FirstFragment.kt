package com.example.moblpr

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
import com.example.moblpr.clients.VeiculoClient
import com.example.moblpr.databinding.FragmentFirstBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class FirstFragment : Fragment() {

    private var seventhPos: TextInputEditText? = null
    private var sixthPos: TextInputEditText? = null
    private var fifthPos: TextInputEditText? = null
    private var fourthPos: TextInputEditText? = null
    private var thirdPos: TextInputEditText? = null
    private var secondPos: TextInputEditText? = null
    private var firstPos: TextInputEditText? = null
    private var dialogLayoutView: View? = null
    private var dialogConfirmation: AlertDialog? = null
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val imageViewModel: ImageUriViewModel by activityViewModels()

    private lateinit var textRecognizer: TextRecognizer
    private var hasImage: Boolean = false

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
            hasImage = true
            binding.cropIv.setImageUriAsync(uri)
        }
    }

    fun alreadyImageSelected(): Boolean {
        return hasImage
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        mainActivity.checkStateButtons()
    }

    fun scan() {
        val mainActivity = activity as MainActivity

        val croppedImage: Bitmap? = binding.cropIv.getCroppedImage()

        croppedImage?.let { img ->
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
                            mainActivity.progressBarShow()
                            scan()
                        }.show()
                }
        }
    }

    private fun showDialogConfirmation(plate: String) {
        val mainActivity = activity as MainActivity
        mainActivity.progressBarHide()

        context?.let {
            dialogLayoutView = LayoutInflater.from(it)
                .inflate(R.layout.license_plate_confirmation_dialog, null, false)

            firstPos = dialogLayoutView?.findViewById(R.id.firstPos)
            secondPos = dialogLayoutView?.findViewById(R.id.secondPos)
            thirdPos = dialogLayoutView?.findViewById(R.id.thirdPos)
            fourthPos = dialogLayoutView?.findViewById(R.id.fourthPos)
            fifthPos = dialogLayoutView?.findViewById(R.id.fifthPos)
            sixthPos = dialogLayoutView?.findViewById(R.id.sixthPos)
            seventhPos = dialogLayoutView?.findViewById(R.id.seventhPos)

            seventhPos!!.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialogConfirmation?.let { dialog ->
                        confirmPlate(dialog)
                    }

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            dialogConfirmation = MaterialAlertDialogBuilder(it)
                .setView(dialogLayoutView)
                .setTitle("Confirmação da Placa")
                .setMessage("Placa Reconhecida")
                .setPositiveButton("Confirmar") { dialog, _ ->
                    confirmPlate(dialog)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }.create()

            firstPos!!.setText(plate[0].toString())
            secondPos!!.setText(plate[1].toString())
            thirdPos!!.setText(plate[2].toString())
            fourthPos!!.setText(plate[3].toString())
            fifthPos!!.setText(plate[4].toString())
            sixthPos!!.setText(plate[5].toString())
            seventhPos!!.setText(plate[6].toString())

            dialogConfirmation?.show()
        }
    }

    private fun confirmPlate(dialog: DialogInterface) {
        val confirmedPlate =
            firstPos!!.text.toString() +
                    secondPos!!.text.toString() +
                    thirdPos!!.text.toString() +
                    fourthPos!!.text.toString() +
                    fifthPos!!.text.toString() +
                    sixthPos!!.text.toString() +
                    seventhPos!!.text.toString()

        dialog.dismiss()

        val mainActivity = activity as MainActivity
        mainActivity.progressBarShow()

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
                    mainActivity.progressBarShow()
                    scan()
                }.show()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}