package com.example.arventurepath.ui.login_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt


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
        registerUser()
        binding.buttonLogin.setOnClickListener {
            if (!isEditTextBlank()){
                if (register){
                    if(!viewModel.isEmailRegistered(binding.editTextEmail.text.toString())){
                        if(binding.editTextPasswordRepeat.text.toString() == binding.editTextPassword.text.toString()){
                            val emailUser = binding.editTextEmail.text.toString()
                            val password = binding.editTextPassword.text.toString()
                            if (verifyEmail(emailUser)){
                                if (verifyPassword(password)){
                                    val passwordEncrypted  = viewModel.hashPassword(password)
                                    hideElements()
                                    viewModel.registerUser(emailUser,passwordEncrypted)
                                }
                            }
                        }else{
                            Toast.makeText(context,"Las contraseñas no coinciden.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context,"Este email ya está registrado.",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    val emailUser = binding.editTextEmail.text.toString()
                    val password = binding.editTextPassword.text.toString()
                    val userId = viewModel.userExist(emailUser,password)
                    if (userId != -1){
                        hideElements()
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToListArventureFragment3(idUser = userId)
                        )
                    }else{
                        Toast.makeText(context,"Usuario no encontrado",Toast.LENGTH_SHORT).show()
                    }

                }
            }


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

    private fun registerUser(){
        lifecycleScope.launch {
            viewModel.idUser.collect{ idRegisterUser ->
                if (idRegisterUser!=-1){
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToTutorialFragment(idUser = idRegisterUser )
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                if (!isLoading) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun hideElements(){
        binding.viewLogin.visibility = View.GONE
        binding.editTextPassword.visibility = View.GONE
        binding.editTextEmail.visibility = View.GONE
        binding.editTextPasswordRepeat.visibility = View.GONE
        binding.textViewRegister.visibility = View.GONE
        binding.imageViewEmail.visibility = View.GONE
        binding.imageViewLock.visibility = View.GONE
        binding.buttonLogin.isEnabled = false
    }

    private fun verifyEmail(mail: String): Boolean {
        // Patrón de expresión regular para verificar el correo electrónico
        val mailPattern = Regex("^[\\w\\.-]+@[a-zA-Z0-9\\.-]+\\.[a-zA-Z]{2,}$")

        // Si es válido, devolvemos true
        return if (mailPattern.matches(mail)) {
            true
        } else {
            // Si no es válido, mostramos un Toast con el mensaje correspondiente
            val errorMessage = "El formato del correo electrónico es incorrecto."
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun verifyPassword(password: String): Boolean {
        val minLength = 8 // Cambia este valor según tu política
        val allowedCharacters = Regex("^[a-zA-Z0-9.!@#\$%^&*()_+=-]*\$") // Caracteres permitidos: letras, números, y algunos especiales

        // Verificar longitud mínima
        if (password.length < minLength) {
            Toast.makeText(context,"La contraseña debe tener al menos $minLength caracteres.",Toast.LENGTH_SHORT).show()
            return false
        }

        // Verificar caracteres permitidos
        if (!password.matches(allowedCharacters)) {
            Toast.makeText(context,"La contraseña contiene caracteres no permitidos.",Toast.LENGTH_SHORT).show()
            return false
        }

        // Verificar al menos un carácter especial
        val specialCharacters = Regex("[.!@#\$%^&*()_+=-]")
        if (!password.contains(specialCharacters)) {
            Toast.makeText(context,"La contraseña debe contener al menos un carácter especial: !@#\$%^&*()_+=-",Toast.LENGTH_SHORT).show()
            return false
        }

        // Si pasa todas las verificaciones, la contraseña es válida
        return true
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