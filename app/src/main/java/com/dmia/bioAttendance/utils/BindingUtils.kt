package com.dmia.bioAttendance.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

/**
 * Converts milliseconds to formatted mm:ss
 *
 * @param value, time in milliseconds.
 */


@BindingAdapter("setImage")
fun setImageFromDrawable(img: AppCompatImageView, imageDrawable: Drawable) {
    img.setImageDrawable(imageDrawable)
}

@BindingAdapter("setTextImage")
fun setTextImageFromDrawable(img: TextView, imageDrawable: Drawable) {
    img.setCompoundDrawablesWithIntrinsicBounds(null, null, imageDrawable, null)
}


@BindingAdapter("bgColor")
fun setBackGround(layout: View, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode))
        layout.setBackgroundColor(Color.parseColor(colorCode))
}


@BindingAdapter("txtColor")
fun setTextColor(layout: AppCompatTextView, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode))
        layout.setTextColor(Color.parseColor(colorCode))
}

@BindingAdapter("txtColor")
fun setTextColor(layout: AppCompatTextView, colorCode: Int?) {
    if (!TextUtils.isEmpty(colorCode.toString()))
        colorCode?.let { layout.setTextColor(it) }
}

@BindingAdapter("txt")
fun setText(layout: AppCompatTextView, text: String?) {
    if (!TextUtils.isEmpty(text))
        text?.let { layout.text = it }
}

@BindingAdapter("imgVector")
fun setVectorImage(layout: AppCompatImageView, image: Int?) {
    if (!TextUtils.isEmpty(image.toString()))
        image?.let { layout.setImageResource(it) }
}


@BindingAdapter("imgTint")
fun setVectorImage(layout: ImageView, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode.toString()))
        colorCode?.let { layout.setColorFilter(Color.parseColor(it)) }
}

@BindingAdapter("ImageTint")
fun setVectorImageColor(imageView: ImageView, colorCode: Int?) {
    if (!TextUtils.isEmpty(colorCode.toString()))
        colorCode?.let { imageView.setColorFilter(it) }
}

@BindingAdapter("setVisibility")
fun visibility(view: View, showView: Boolean) {
    if (showView) view.visibility = View.VISIBLE else view.visibility = View.GONE
}

@BindingAdapter("bgTint")
fun setBackGroundTint(layout: ConstraintLayout, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode))
        ViewCompat.setBackgroundTintList(
            layout,
            ColorStateList.valueOf(Color.parseColor(colorCode))
        );
}

@BindingAdapter("bgColor")
fun setBackGroundColor(layout: ConstraintLayout, colorCode: Int?) {
    ViewCompat.setBackgroundTintList(
        layout,
        colorCode?.let { ColorStateList.valueOf(it) }
    );
}

@BindingAdapter("bgColor")
fun setBackGroundColor(layout: ConstraintLayout, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode)) {
        layout.setBackgroundColor(Color.parseColor(colorCode))
    }
}

@BindingAdapter("margin")
fun setMargin(layout: View, dime: Int) {
    layout.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, dime)
}


@BindingAdapter("setProgressBar")
fun setProgessBar(progressBar: ProgressBar, progress: String?) {
    if (progress != null) {
        progressBar.progress = progress.toDouble().toInt()
    }
}


@BindingAdapter("setRecyclerviewOrientation")
fun setRecyclerviewOrientation(rv: RecyclerView, orientation: Int) {

    if (orientation == LinearLayoutManager.HORIZONTAL)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
    else
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)

}

@BindingAdapter("layoutMarginTop")
fun setLayoutMarginTop(layout: View, dimen: Float) {
    val layoutParams = layout.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.topMargin = dimen.toInt()
    layout.layoutParams = layoutParams
}

@BindingAdapter("layoutMarginStart")
fun setLayoutMarginStart(layout: View, dimen: Float) {
    val layoutParams = layout.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginStart = dimen.toInt()
    layout.layoutParams = layoutParams
}

@BindingAdapter("layoutMarginBottom")
fun setLayoutMarginBottom(layout: View, dimen: Float) {
    val layoutParams = layout.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.bottomMargin = dimen.toInt()
    layout.layoutParams = layoutParams
}

@BindingAdapter("layoutConstraintBottomtoTopOf")
fun setBottomConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.bottomToTop = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintToptoBottomOf")
fun setTopConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.topToBottom = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintToptoTopOf")
fun setTToTConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.topToTop = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintBottomtoBottomOf")
fun setBTOBConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.bottomToBottom = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintStartToEndOf")
fun setSTOEConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.startToEnd = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintStartToStartOf")
fun setSTOSConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.startToStart = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintEndToEndOf")
fun setETOEConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.endToEnd = resource
    layout.layoutParams = params
}

@BindingAdapter("layoutConstraintEndToStartOf")
fun setETOSConstraint(layout: View, resource: Int) {
    val params = layout.layoutParams as ConstraintLayout.LayoutParams
    params.startToStart = resource
    layout.layoutParams = params
}


@BindingAdapter("layoutHeight")
fun setLayoutHeight(layout: View, dimen: Float) {
    val params = layout.layoutParams as ViewGroup.LayoutParams
    params.height = dimen.toInt()
    layout.layoutParams = params
}

@BindingAdapter("bottomSheetBehaviorPeekHeight")
fun setState(v: View, dimen: Float) {
    val viewBottomSheetBehavior = BottomSheetBehavior.from(v)
    viewBottomSheetBehavior.peekHeight = dimen.toInt()
}


@BindingAdapter("strikeThrough")
fun strikeThrough(textView: TextView, strikeThrough: Boolean) {
    if (strikeThrough) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

