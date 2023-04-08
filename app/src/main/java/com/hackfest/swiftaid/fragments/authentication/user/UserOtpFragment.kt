package com.hackfest.swiftaid.fragments.authentication.user

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentUserOtpBinding
import java.util.concurrent.TimeUnit


class UserOtpFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private var mCounterDown: CountDownTimer? = null
    private lateinit var OTP: String
    private lateinit var binding: FragmentUserOtpBinding
    private lateinit var progressDialog: ProgressDialog
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserOtpBinding.inflate(inflater, container, false)

        // Retrieved OTP, phone number and resending token from UserLoginFragment
        OTP = arguments?.getString("OTP").toString()
        phoneNumber = arguments?.getString("Phone Number")!!.toString()
        arguments?.let {
            resendToken = it.getParcelable("resend")!!
        }
        setSpannableString()
        showTimer(60000)
        initAuth()
        // Inflate the layout for this fragment
        return (binding.root)
    }

    private fun initAuth() {
        binding.btnVerify.setOnClickListener {
            Log.e("OTP", OTP)
            val typedOTP = binding.etCode.text.toString()
            Log.e("code", typedOTP)
            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credentials: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(OTP, typedOTP)
                    signInWithPhoneAuthCredential(credentials)
                } else {
                    Toast.makeText(this.context, "Please Enter Correct OTP", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this.context, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnResend.isEnabled = false

        //Delaying visibility of Resend Button by 60 sec
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            binding.btnResend.isEnabled = true
        }, 60000)

        binding.btnResend.setOnClickListener {
            if (resendToken != null) {
                showTimer(60000)
//                progressDialog = createProgressDialog("Sending verification code ...", false)
//                progressDialog.show()
                resendVerificationCode()
            }
        }
    }

    private fun resendVerificationCode() {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this.requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken!!)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.d(ContentValues.TAG, "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("OTP", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            // Create a bundle to pass the verification ID and resending token to the OTP fragment
            OTP = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.nearByFragment)
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


    private fun setSpannableString() {

        val span = SpannableString(getString(R.string.waiting_text, phoneNumber))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                showUserLoginFragment()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length - 14, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvVerify.movementMethod = LinkMovementMethod.getInstance()
        binding.tvVerify.text = span
    }

    private fun showUserLoginFragment() {
        findNavController().navigate(R.id.action_userOtpFragment_to_userLoginFragment)
    }

    private fun showTimer(milliSecInFuture: Long) {
        binding.btnResend.isEnabled = false
        mCounterDown = object : CountDownTimer(milliSecInFuture, 1000) {
            override fun onTick(p0: Long) {
                binding.tvCounter.isVisible = true
                binding.tvCounter.text = "Seconds Remaining : ${p0 / 1000}"
            }

            override fun onFinish() {
                binding.btnResend.isEnabled = true
                binding.tvCounter.isVisible = false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCounterDown != null) {
            mCounterDown!!.cancel()
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

