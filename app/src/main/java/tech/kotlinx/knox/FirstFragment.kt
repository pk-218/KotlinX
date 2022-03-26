package tech.kotlinx.knox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.kotlinx.knox.databinding.FragmentFirstBinding
import tech.kotlinx.knox.ui.viewmodels.FirstViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment() : Fragment() {

    private val viewModel: FirstViewModel by viewModels()
    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = binding.userName.text.toString()
        binding.buttonFirst.setOnClickListener {
            viewModel.saveUserName(userName)
            val bundle = bundleOf("userName" to userName)
            findNavController().navigate(R.id.action_FirstFragment_to_ConnectionDetailsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}