package com.example.moblpr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.moblpr.MainActivity
import com.example.moblpr.R
import com.example.moblpr.databinding.FragmentVehicleBinding
import com.example.moblpr.models.Vehicle

class VehicleFragment : Fragment() {

    private var _binding: FragmentVehicleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var vehicle: Vehicle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity

        vehicle = arguments?.getSerializable("vehicle") as Vehicle

        binding.placaEdt.setText(vehicle!!.placa)
        binding.valorEdt.setText(vehicle!!.valor)
        binding.marcaEdt.setText(vehicle!!.marca)
        binding.modeloEdt.setText(vehicle!!.modelo)
        binding.anoModeloEdt.setText(vehicle!!.anoModelo)
        binding.combustivelEdt.setText(vehicle!!.combustivel)
        binding.codigoFipeEdt.setText(vehicle!!.codigoFipe)
        binding.tipoVeiculoEdt.setText(vehicle!!.tipoVeiculo)
        binding.cilindradasEdt.setText(vehicle!!.cilindradas)
        binding.potenciaEdt.setText(vehicle!!.potencia)
        binding.chassiEdt.setText(vehicle!!.chassi)
        binding.corEdt.setText(vehicle!!.cor)
        binding.municipioEdt.setText(vehicle!!.municipio)
        binding.ufEdt.setText(vehicle!!.uf)
        binding.renavamEdt.setText(vehicle!!.renavam)
        binding.ipvaEdt.setText(vehicle!!.ipva)

        mainActivity.progressBarHide()
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        mainActivity.visibilityButtonsVehicleFrag()
    }

    fun generateQrCode() {
        val bundle = Bundle()
        bundle.putSerializable("vehicle", vehicle)

        findNavController().navigate(R.id.action_carInfoFragment_to_SecondFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}