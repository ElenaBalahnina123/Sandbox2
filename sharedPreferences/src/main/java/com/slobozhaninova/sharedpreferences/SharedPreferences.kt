package com.slobozhaninova.sharedpreferences

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.slobozhaninova.sharedpreferences.databinding.SharedPraferencesBinding

class SharedPreferences : Fragment(R.layout.shared_praferences) {

    private lateinit var binding: SharedPraferencesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = SharedPraferencesBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            "PreferencesFilename",
            masterKey,
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()

        binding.apply {
            btnSave.setOnClickListener {
                val userName = edtUsername.text.toString()
                val email = edtEmail.text.toString()

                editor.apply {
                    putString("user_name",userName)
                    putString("email",email)
                    apply()
                }
            }

            btnLoad.setOnClickListener {

                val userName = sharedPreferences.getString("user_name",null)
                val email = sharedPreferences.getString("email",null)

                tvUsername.text = userName
                tvEmail.text=email
            }
        }
        return view
    }



}