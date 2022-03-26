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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tech.kotlinx.knox.databinding.FragmentConnectionDetailsBinding
import java.net.NetworkInterface
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ConnectionDetailsFragment : Fragment() {

    private var _binding: FragmentConnectionDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        var userIpAddress: String? = "0.0.0.0"
        val connectivityManager: ConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        when (connectivityManager.activeNetworkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> userIpAddress = getWifiIpAddress()
            ConnectivityManager.TYPE_MOBILE -> userIpAddress = getMobileDataIpAddress()
        }
        binding.senderIpAddressField.setText(userIpAddress)

        binding.enterChatButton.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_chatFragment)
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
                                result.substring(0, delimiter).uppercase(Locale.getDefault());
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