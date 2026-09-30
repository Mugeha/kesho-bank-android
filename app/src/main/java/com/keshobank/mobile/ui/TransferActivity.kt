package com.keshobank.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.databinding.ActivityTransferBinding

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_PREFILL_RECIPIENT)?.let { binding.recipientInput.setText(it) }
        intent.getStringExtra(EXTRA_PREFILL_AMOUNT)?.let { binding.amountInput.setText(it) }

        binding.continueButton.setOnClickListener {
            val recipient = binding.recipientInput.text.toString().trim()
            val amountText = binding.amountInput.text.toString().trim()
            val amount = amountText.toDoubleOrNull()

            var valid = true
            if (recipient.length < 6) {
                binding.recipientInput.error = "Enter a valid recipient account number"
                valid = false
            }
            if (amount == null || amount <= 0.0) {
                binding.amountInput.error = "Enter a valid amount"
                valid = false
            }
            if (!valid) return@setOnClickListener

            val intent = Intent(this, TransferConfirmActivity::class.java)
                .putExtra(TransferConfirmActivity.EXTRA_RECIPIENT, recipient)
                .putExtra(TransferConfirmActivity.EXTRA_AMOUNT, amountText)
                .putExtra(TransferConfirmActivity.EXTRA_MEMO, binding.memoInput.text.toString())
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_PREFILL_RECIPIENT = "extra_prefill_recipient"
        const val EXTRA_PREFILL_AMOUNT = "extra_prefill_amount"
    }
}
