package com.polidea.cockpit.paramsedition.viewholder

import android.view.View
import android.widget.EditText
import com.polidea.cockpit.R
import com.polidea.cockpit.core.CockpitParam
import com.polidea.cockpit.utils.TextWatcherAdapter

class StringParamViewHolder(view: View) : ParamBaseViewHolder<String>(view) {

    private val value: EditText = view.findViewById(R.id.cockpit_string_param_value)

    init {
        value.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                valueChangeListener?.invoke(s?.toString() ?: "")
            }
        })
    }

    override fun displayParam(param: CockpitParam<String>) {
        super.displayParam(param)
        value.setText(param.value)
    }
}