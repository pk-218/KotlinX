package tech.kotlinx.knox

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tech.kotlinx.knox.databinding.FragmentConnectionDetailsBinding
import java.net.NetworkInterface
import java.util.*


@AndroidEntryPoint
class ConnectionDetailsFragment : Fragment() {

    private var _binding: FragmentConnectionDetailsBinding? = null
    private val args by navArgs<ConnectionDetailsFragmentArgs>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FragmentArgs", args.userName)
        Toast.makeText(context, "Welcome ${args.userName}", Toast.LENGTH_SHORT).show()

        var userIpAddress: String? = "0.0.0.0"
        val connectivityManager: ConnectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        when (connectivityManager.activeNetworkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> userIpAddress = getWifiIpAddress()
            ConnectivityManager.TYPE_MOBILE -> userIpAddress = getMobileDataIpAddress()
        }
        binding.senderIpAddressField.setText(userIpAddress)

        binding.enterChatButton.setOnClickListener {
            val bundle = bundleOf(
                "receiverIP" to binding.receiverIpAddressText.text.toString(),
                "receiverPort" to 5000,
                "senderUserName" to args.userName
            )
            findNavController().navigate(
                R.id.action_ConnectionDetailsFragment_to_ChatFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION")
    private fun getWifiIpAddress(): String? {
        val wifiMgr = context?.getSystemService(WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiMgr!!.connectionInfo
        val ip = wifiInfo.ipAddress
        return Formatter.formatIpAddress(ip)
    }

    private fun getMobileDataIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val addresses = Collections.list(networkInterface.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val result = address.hostAddress!!
                        val isIPv4 = result.indexOf(':') < 0
                        return if (isIPv4)
                            result
                        else {
                            val delimiter: Int = result.indexOf('%')
                            if (delimiter < 0)
                                result.uppercase(Locale.getDefault())
                            else
                                result.substring(0, delimiter).uppercase(Locale.getDefault())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("MobileIPError", e.toString())
        }
        return "0.0.0.0"
    }
}