package fr.thedestiny.sms.android.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogUtil {

	public static void showAlertDialog(final Context ctx, final int title, final int message) {
		getBuilder(ctx)
		.setTitle(title)
		.setMessage(message)
		.create()
		.show();
	}
	
	public static void showAlertDialog(final Context ctx, final CharSequence title, final CharSequence message) {
		getBuilder(ctx)
		.setTitle(title)
		.setMessage(message)
		.create()
		.show();
	}
	
	private static Builder getBuilder(final Context ctx) {
		return new AlertDialog.Builder(ctx)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}); 
	}
}
