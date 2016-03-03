package ua.nau.edu.API;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.R;

public class APIDialogs {
    public static class AlertDialogs {

        public static void customDialog(final Context context, String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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

        public static void internetConnectionError(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle("Ошибка")
                    .setMessage("Нету соединения с Интернетом. Пожалуйста, проверьте настройки сети.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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

        public static void serverConnectionError(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle("Ошибка")
                    .setMessage("Невозможно соединиться с сервером. Пожалуйста, попробуйте позже.")
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
                    .setTitle("Ошибка")
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
                    .setTitle("Ошибка")
                    .setMessage("Произошла ошибка при отправке записи. Пожалуйста, попробуйте еще раз.")
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
                    .setTitle("Ошибка")
                    .setMessage("Произошла ошибка при обновлении записи. Пожалуйста, попробуйте еще раз.")
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
                    .setTitle("Ошибка")
                    .setMessage("Произошла ошибка при удалении записи.")
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
                    .setTitle("Ошибка")
                    .setMessage("Введите сообщение.")
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
                    .setTitle("Ошибка")
                    .setMessage("Максимальная длина сообщения - " + Integer.toString(APIValues.maxMessageLength) + " символов.")
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

        public static void gpsDisabled(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder
                    .setMessage("Для определения местоположения необходимо включить GPS. Включить GPS сейчас?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                }
            });

            dialog.show();
        }

        public static void wifiDisabled(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder
                    .setMessage("Для определения местоположения необходимо включить Wifi. Включить Wifi сейчас?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                }
            });

            dialog.show();
        }
    }

    public static class ProgressDialogs {
        public interface ProgressDialogCallbackInterface {
            void onCancel();
        }

        public static MaterialDialog loadingCancelable(final Context context, final ProgressDialogCallbackInterface callback) {
            MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                    .content(context.getResources().getString(R.string.dialog_loading))
                    .progress(true, 0)
                    .widgetColor(ContextCompat.getColor(context, R.color.colorAppPrimary))
                    .contentColor(ContextCompat.getColor(context, R.color.black))
                    .backgroundColor(ContextCompat.getColor(context, R.color.white))
                    .cancelable(true)
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.d("loadingCancelable", "onCancel()");
                            if (callback != null) {
                                callback.onCancel();
                            }
                        }
                    })
                    .build();

            return materialDialog;
        }

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
