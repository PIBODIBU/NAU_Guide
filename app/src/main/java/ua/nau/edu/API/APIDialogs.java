package ua.nau.edu.API;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.afollestad.materialdialogs.MaterialDialog;

import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.R;

public class APIDialogs {
    public static class AlertDialogs {
        public static void internetConnectionErrorWithExit(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle("Ошибка")
                    .setMessage("Нету соединения с Интернетом. Пожалуйста, проверьте настройки сети.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    })
                    .setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void serverConnectionErrorWithExit(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle("Ошибка")
                    .setMessage("Невозможно соединиться с сервером. Пожалуйста, попробуйте позже.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    })
                    .setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void badLoginOrUsername(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Неправильный логин или пароль. Пожалуйста, попробуйте еще раз.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void errorWhilePostingMessage(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Ошибка при отправке записи. Пожалуйста, попробуйте еще раз.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void errorWhileUpdatingMessage(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Ошибка при обновлении записи. Пожалуйста, попробуйте еще раз.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void errorWhileDeletingPost(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Ошибка при удалении записи.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void emptyString(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Ошибка. Введите сообщение.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }

        public static void tooLongMeassage(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setMessage("Ошибка. Максимальная длина сообщения - 300 символов.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                }
            });
            dialog.show();
        }
    }

    public static class ProgressDialogs {

        public static MaterialDialog loading(final Context context) {
            return new MaterialDialog.Builder(context)
                    .content(context.getResources().getString(R.string.dialog_loading))
                    .progress(true, 0)
                    .cancelable(false)
                    .widgetColor(ContextCompat.getColor(context, R.color.colorAppPrimary))
                    .contentColor(ContextCompat.getColor(context, R.color.black))
                    .backgroundColor(ContextCompat.getColor(context, R.color.white))
                    .build();

        }
    }

}
