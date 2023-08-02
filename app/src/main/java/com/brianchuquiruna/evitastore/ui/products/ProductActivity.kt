package com.brianchuquiruna.evitastore.ui.products

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.brianchuquiruna.evitastore.R
import com.brianchuquiruna.evitastore.databinding.ActivityProductBinding
import com.brianchuquiruna.evitastore.ui.products.ProductViewModel
import com.brianchuquiruna.evitastore.ui.signup.SignUpActivity

class ProductActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context): Intent =
            Intent(context, ProductActivity::class.java)
    }

    private lateinit var binding: ActivityProductBinding
    private val loginViewModel : ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
    }
}
