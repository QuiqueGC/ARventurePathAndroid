package com.example.arventurepath.ui.login_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var register: Boolean = false
    private val viewModel = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getListUsers()

        binding.buttonLogin.setOnClickListener {
            if (!isEditTextBlank()){
                if (register){
                    if(!viewModel.isEmailRegistered(binding.editTextEmail.text.toString())){
                        if(binding.editTextPasswordRepeat.text.toString() == binding.editTextPassword.text.toString()){
                            Toast.makeText(context,"REGISTRO EFECTUADO",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,"Las contraseñas no coinciden.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context,"Este email ya está registrado.",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    val emailUser = binding.editTextEmail.text.toString()
                    val passwordEncrypted = viewModel.hashPassword(binding.editTextPassword.text.toString())
                    if (viewModel.userExist(emailUser,passwordEncrypted) != -1){
                        Toast.makeText(context,"LOGIN EFECTUADO",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Usuario no encontrado",Toast.LENGTH_SHORT).show()
                    }

                }
            }

            //findNavController().navigate(
            //    LoginFragmentDirections.actionLoginFragmentToListArventureFragment3(idUser = 0)
            //)
        }
        binding.textViewRegister.setOnClickListener {
            binding.editTextEmail.setText("")
            binding.editTextPassword.setText("")
            binding.editTextPasswordRepeat.setText("")
            if (!register){
                binding.editTextPasswordRepeat.visibility = View.VISIBLE
                binding.textViewRegister.text = getString(R.string.noregisterText)
                binding.buttonLogin.text = getString(R.string.register)
                register = true
            }else{
                binding.editTextPasswordRepeat.visibility = View.GONE
                binding.textViewRegister.text = getString(R.string.registerText)
                binding.buttonLogin.text = getString(R.string.login)
                register = false
            }
        }
    }

    private fun isEditTextBlank():Boolean{
        if (binding.editTextEmail.text.toString() == ""){
            return true
        }
        if (binding.editTextPassword.text.toString() == ""){
            return true
        }
        if (register){
            if (binding.editTextPasswordRepeat.text.toString() == ""){
                return true
            }
        }
        return false
    }

}