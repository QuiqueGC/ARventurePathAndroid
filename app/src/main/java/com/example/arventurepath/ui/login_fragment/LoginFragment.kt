package com.example.arventurepath.ui.login_fragment

import android.os.Bundle
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


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var register: Boolean = false
    private val viewModel = LoginViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        //launchExternalApk("com.DefaultCompany.Intento1")


        return binding.root

    }

    /*private val contractRA: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Aquí puedes manejar el resultado de la grabación de audio
                val data: Intent? = result.data
                // Procesar los datos si es necesario
            } else {
                // Si la acción se cancela o falla, aquí puedes manejarlo
            }
        }

    private fun launchExternalApk(packageName: String) {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            contractRA.launch(it)
        } ?: run {
            // Manejar el caso en el que no se pueda lanzar la actividad
            Toast.makeText(requireContext(), "No se pudo iniciar la aplicación", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun startLikeRaceQuiz() {
        val packageName = "com.DefaultCompany.Intento1"
        val packageManager = requireActivity().packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if(launchIntent != null){
            startActivity(launchIntent)
        }
    }*/

    /*private fun runApk(apkResourceId: Int) {
        val apkInputStream = resources.openRawResource(apkResourceId)
        val apkFile = File(requireContext().filesDir, "temp.apk") // Guarda temporalmente la APK
        apkInputStream.copyTo(apkFile.outputStream())

        val apkUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Para abrir en una nueva tarea
        }
        startActivity(intent)
    }*/

    /*fun getPackageNameFromApk(apkResourceId: Int): String? {
        val packageManager = requireContext().packageManager
        val packageName: String?
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(
            "android.resource://" + requireContext().packageName + "/" + apkResourceId,
            PackageManager.GET_META_DATA
        )
        if (packageArchiveInfo != null) {
            packageName = packageArchiveInfo.packageName
        } else {
            packageName = null
        }
        return packageName
    }*/

    /*fun launchUnityApp(view: View) {
        val unityPackageUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.your_unity_apk)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(unityPackageUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }*/
    /* private fun tryingApk() {
         val apkInputStream = resources.openRawResource(R.raw.apk_ra)
         val apkFile = File(requireContext().filesDir, "apk_ra")
         apkInputStream.copyTo(apkFile.outputStream())
         val apkUri = FileProvider.getUriForFile(requireContext(), "com.example.arventurepath.ui.login_fragment.LoginFragment.FileProvider", apkFile)
         val intent = Intent(Intent.ACTION_VIEW).apply {
             setDataAndType(apkUri, "application/vnd.android.package-archive")
             addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
         }
         startActivity(intent)
     }*/

    /*private fun launchInstalledApp(packageName: String) {
        val packageManager = requireActivity().packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        } else {
            // La aplicación no está instalada
            Toast.makeText(requireContext(), "La aplicación no está instalada", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getListUsers()
        registerUser()
        binding.buttonLogin.setOnClickListener {
            if (!isEditTextBlank()) {
                if (register) {
                    if (!viewModel.isEmailRegistered(binding.editTextEmail.text.toString())) {
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
                    if(viewModel.isEmailRegistered(emailUser)){
                        val userId = viewModel.userExist(emailUser,password)
                        if (userId != -1){
                            hideElements()
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToListArventureFragment3(idUser = userId)
                            )
                        }else{
                            Toast.makeText(context,"Contraseña incorrecta.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context,"Email no encontrado.",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(context,"Rellene todos los campos.",Toast.LENGTH_SHORT).show()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.idUser.collect { idRegisterUser ->
                if (idRegisterUser != -1) {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToTutorialFragment(idUser = idRegisterUser)
                    )
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
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