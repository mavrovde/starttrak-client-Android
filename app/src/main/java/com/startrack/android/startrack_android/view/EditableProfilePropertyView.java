package com.startrack.android.startrack_android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.startrack.android.startrack_android.R;
import com.startrack.android.startrack_android.model.EditableProfileProperty;

/**
 * Created by vrogovskiy on 2/15/16.
 */
public class EditableProfilePropertyView extends RelativeLayout {
    private TextView propertyName;
    private EditText propertyValueEditText;

    public EditableProfilePropertyView(Context context) {
        super(context);
        init();
    }

    public EditableProfilePropertyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditableProfilePropertyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

/*    public SelectableProfilePropertyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    public EditableProfilePropertyView(Context context, EditableProfileProperty editableProfileProperty) {
        super(context);
        init();
        this.propertyName.setText(editableProfileProperty.getProfilePropertyName());
        this.propertyValueEditText.setHint(editableProfileProperty.getProfilePropertyName());
        if (editableProfileProperty.getPropertyValue() != null) {
            this.propertyValueEditText.setText(editableProfileProperty.getPropertyValue());
        } else {
            this.propertyValueEditText.setText("");
        }
        Typeface custom_font_regular = Typeface.createFromAsset(context.getAssets(), "fonts/Bariol_Regular.otf");
        this.propertyName.setTypeface(custom_font_regular);
        this.propertyValueEditText.setTypeface(custom_font_regular);
//        this.propertyValueEditText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        EditableProfilePropertyViewHeigh = h;
    }

    public static int EditableProfilePropertyViewHeigh;

    private void init() {
        inflate(getContext(), R.layout.editable_property_item, this);
        this.propertyName = (TextView) findViewById(R.id.propertyName);
        this.propertyValueEditText = (EditText) findViewById(R.id.propertyValueEditText);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol_Regular.otf");
        this.propertyName.setTypeface(custom_font);
        this.propertyValueEditText.setTypeface(custom_font);

        this.propertyValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    propertyName.setVisibility(VISIBLE);
                else propertyName.setVisibility(INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public String getCurrentValue() {
        return this.propertyValueEditText.getText().toString();
    }

}
