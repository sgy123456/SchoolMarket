package com.nupt.shrimp.schoolmarket.utils.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nupt.shrimp.schoolmarket.R;


public class BuyCartCustomDialog extends Dialog {

	public BuyCartCustomDialog(Context context) {
		super(context);
	}

	public BuyCartCustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
//		private String message;
		private int num;

		private String positiveButtonText;
		private String negativeButtonText;
//		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		private Button positiveButton;
		private Button negativeButton;
		private TextView titleTextView;
		public Builder(Context context) {
			this.context = context;
		}

		public Builder setNum(int num) {
			this.num = num;
			return this;
		}

		/**
		 * 设置对话框标题
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * 设置对话框标题
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}



		/**
		 * 设置取消按钮监听
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * 创建对话框
		 * @return
		 */
		public BuyCartCustomDialog create() {
			// instantiate the dialog with the custom Theme
			final BuyCartCustomDialog dialog = new BuyCartCustomDialog(context, R.style.BuyCartDialog);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
			positiveButton = (Button) layout.findViewById(R.id.positiveButton);
			negativeButton = (Button) layout.findViewById(R.id.negativeButton);

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// 设置确认按钮
			if (positiveButtonText != null) {
				positiveButton.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					positiveButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				// 如果没有确认按钮，让其不可见
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				negativeButton.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					negativeButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negativeButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
//			if (message != null) {
//				((TextView) layout.findViewById(R.id.message)).setText(message);
//			} else if (contentView != null) {
//				// if no message set
//				// add the contentView to the dialog body
//				((LinearLayout) layout.findViewById(R.id.content))
//						.removeAllViews();
//				((LinearLayout) layout.findViewById(R.id.content)).addView(
//						contentView, new LayoutParams(
//								LayoutParams.MATCH_PARENT,
//								LayoutParams.MATCH_PARENT));
//			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
