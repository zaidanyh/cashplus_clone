package com.pasukanlangit.cashplus.ui.cartproduct

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemProductCartBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.utils.MyUtils

class CartAdapter(private val cartEvent: CartEvent) :
    ListAdapter<ProductStoreDataItem, CartAdapter.CartViewHolder>(DIFF_UTIL
) {
    class CartViewHolder(val binding: ItemProductCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ItemProductCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            val context = root.context
            Glide.with(context)
                .load(product.image1)
                .thumbnail(
                    Glide.with(context)
                        .load(R.raw.image_loading_state)
                )
                .error(R.drawable.ic_empty)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(ivProduct)

            tvTitleProduct.text = product.productName
            edtQty.setText(product.qty.toString())
            tvPriceProduct.text = MyUtils.getNumberRupiah(product.price.toDoubleOrNull() ?: 0.0)

            cbItem.isChecked = product.isChecked

            if (product.note.isNotEmpty()) addNote.text = context.getString(R.string.label_note, product.note) //"Catatan : ${product.note}"

            checkButtonIncAndDec(product.qty, product.stock, holder)

            btnIncreaseQty.setOnClickListener {
                val currentQty = product.qty
                val newQtyValue = currentQty.inc()
                if (newQtyValue <= product.stock.toInt()) {
                    updateQty(newQtyValue, product.productId, edtQty)
                    checkButtonIncAndDec(newQtyValue, product.stock, holder)
                }
            }

            btnDecreaseQty.setOnClickListener {
                val currentQty = product.qty
                val newQtyValue = currentQty.dec()
                if (newQtyValue >= 1) {
                    updateQty(newQtyValue, product.productId, edtQty)
                    checkButtonIncAndDec(newQtyValue, product.stock, holder)
                }
            }

            edtQty.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    var currentQty = edtQty.text.toString().toInt()
                    if (currentQty > product.stock.toInt()) {
                        currentQty = product.stock.toInt()
                    }
                    if (currentQty < 1) {
                        currentQty = 1
                    }
                    updateQty(currentQty, product.productId, edtQty)
                }
                false
            }

            addNote.setOnClickListener {
                edtNote.isVisible = !edtNote.isVisible
            }
            cbItem.setOnClickListener {
                setCheckedProduct(product.productId, cbItem.isChecked)
            }

            btnDeleteCart.setOnClickListener {
                cartEvent.deleteCart(product)
            }
            edtNote.onDone {
                updateNote(context, product.productId, edtNote.text.toString(), addNote)
                edtNote.hideKeyboard()
                edtNote.isVisible = false
            }
        }
    }

    private fun updateNote(context: Context, productId: String, note: String, labelNote: TextView) {
        labelNote.text = context.getString(R.string.label_note, note) //"Catatan : $note"
        val productUpdated = currentList.single { it.productId == productId }
        productUpdated.note = note
        cartEvent.updateCart(productUpdated)
    }

    private fun checkButtonIncAndDec(qty: Int, stock: String, holder: CartViewHolder) {
        with(holder.binding) {
            btnDecreaseQty.isEnabled = qty > 1
            btnIncreaseQty.isEnabled = qty < stock.toInt()
        }
    }

    private fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun EditText.onDone(callback: () -> Unit) {
        // These lines optional if you don't want to set in Xml
        imeOptions = EditorInfo.IME_ACTION_DONE
        maxLines = 1
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
//                true
            }
            false
        }
    }

    fun getProductChecked(): List<ProductStoreDataItem> = currentList.filter { it.isChecked }

    private fun updateQty(newQty: Int, productId: String, edt: EditText) {
        edt.setText(newQty.toString())
        val productUpdated = currentList.single { it.productId == productId }
        productUpdated.qty = newQty
        cartEvent.updateCart(productUpdated)
    }

    private fun setCheckedProduct(productId: String, isChecked: Boolean) {
        val productUpdated = currentList.single { it.productId == productId }
        productUpdated.isChecked = isChecked
        cartEvent.updateCart(productUpdated)
    }

    fun allProductIsChecked(value: Boolean) {
        currentList.forEach {
            it.isChecked = value
        }
    }

    interface CartEvent {
        fun updateCart(productStoreDataItem: ProductStoreDataItem)
        fun deleteCart(productStoreDataItem: ProductStoreDataItem)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<ProductStoreDataItem>() {
            override fun areItemsTheSame(
                oldItem: ProductStoreDataItem,
                newItem: ProductStoreDataItem
            ): Boolean = oldItem.productId == newItem.productId

            override fun areContentsTheSame(
                oldItem: ProductStoreDataItem,
                newItem: ProductStoreDataItem
            ): Boolean = oldItem == newItem
        }
    }


}