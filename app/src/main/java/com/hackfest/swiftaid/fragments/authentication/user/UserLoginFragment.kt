package com.hackfest.swiftaid.fragments.authentication.user

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentUserLoginBinding
import java.util.concurrent.TimeUnit


class UserLoginFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private var phoneNumber: String = ""
    private lateinit var number: String
    private lateinit var binding: FragmentUserLoginBinding
    private var OTP: String = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var bundle: Bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserLoginBinding.inflate(inflater, container, false)
        val nc = findNavController()


//        mProgressbar = binding.phoneProgressBar
//        mProgressbar.visibility = View.INVISIBLE
        binding.sendOtpButton.setOnClickListener {

            number = binding.phoneInput.text.toString()
            phoneNumber = binding.countryCodeLabel.text.toString() + number
            Log.e("number", number)
            if (number.isNotEmpty()) {
                bundle.putString("Phone Number", phoneNumber)
                progressDialog = createProgressDialog("Please wait....", false)
                progressDialog.show()
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this.requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            } else {
                Toast.makeText(this.context, "Please Enter Your Number", Toast.LENGTH_SHORT).show()
                binding.sendOtpButton.isEnabled = false
            }

            binding.continueButton.setOnClickListener {
                Log.e("otp", OTP)
                bundle.putString("OTP", OTP)
                bundle.putParcelable("resend", resendToken)
                nc.navigate(R.id.action_userLoginFragment_to_userOtpFragment, bundle)
            }
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)

        binding.googleSignInButton.setOnClickListener {
            signInGoogle()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)

            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this.context, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //Update the UI
                Toast.makeText(this.context, "Logged In Successfully !", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.SOSFragment)
            } else {
                Toast.makeText(this.context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this.context, "Authentication Successful", Toast.LENGTH_SHORT)
                        .show()

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                    }
                    // Update UI
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.e(ContentValues.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }


        override fun onVerificationFailed(e: FirebaseException) {
            progressDialog.dismiss()
            Toast.makeText(context,"Please Sign In through GOOGLE", Toast.LENGTH_SHORT).show()
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.e(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.e(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.e(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.e(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.e("verification", "onCodeSent:$verificationId")
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            OTP = verificationId
            resendToken = token
            binding.sendOtpButton.visibility = View.INVISIBLE
            binding.continueButton.visibility = View.VISIBLE

            progressDialog.dismiss()
//                if (::resendToken.isInitialized) {
//                     putParcelable("resendToken", resendToken)
//                 }


            // Save verification ID and resending token so we can use them later
            // Create a bundle to pass the verification ID and resending token to the OTP fragment
//            val action = UserLoginFragmentDirections.actionUserLoginFragmentToUserOTPFragment2(verificationId)


        }

    }

    fun Fragment.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
        return android.app.ProgressDialog(this.requireContext()).apply {
            setCancelable(false)
            setMessage(message)
            setCanceledOnTouchOutside(false)
        }


    }
}