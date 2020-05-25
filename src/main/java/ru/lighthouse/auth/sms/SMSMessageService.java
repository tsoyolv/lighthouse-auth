package ru.lighthouse.auth.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Math;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Service
public class SMSMessageService {

    @Value("${sms.serviceEnabled}")
    private boolean smsServiceEnabled;

    @Value("${sms.login}")
    private String smscLogin;

    @Value("${sms.password}")
    private String smscPassword;

    @Value("${sms.https}")
    private boolean smscHttps;

    @Value("${sms.charset}")
    private String smscCharset;

    @Value("${sms.debug}")
    private boolean smscDebug;

    @Value("${sms.post}")
    private boolean smscPost;

    public String[] sendSmsImmediately(String phones, String message, int translit) {
        return sendSms(phones, message, translit, "", "", 0, "", "");
    }

    /**
     * Отправка SMS
     *
     * @param phones   - список телефонов через запятую или точку с запятой
     * @param message  - отправляемое сообщение
     * @param translit - переводить или нет в транслит (1,2 или 0)
     * @param time     - необходимое время доставки в виде строки (DDMMYYhhmm, h1-h2, 0ts, +m)
     * @param id       - идентификатор сообщения. Представляет собой 32-битное число в диапазоне от 1 до 2147483647.
     * @param format   - формат сообщения (0 - обычное sms, 1 - flash-sms, 2 - wap-push, 3 - hlr, 4 - bin, 5 - bin-hex, 6 - ping-sms, 7 - mms, 8 - mail, 9 - call, 10 - viber, 11 - soc)
     * @param sender   - имя отправителя (Sender ID). Для отключения Sender ID по умолчанию необходимо в качестве имени передать пустую строку или точку.
     * @param query    - строка дополнительных параметров, добавляемая в URL-запрос ("valid=01:00&maxsms=3&tz=2")
     * @return array (<id>, <количество sms>, <стоимость>, <баланс>) в случае успешной отправки
     * или массив (<id>, -<код ошибки>) в случае ошибки
     */
    public String[] sendSms(String phones, String message, int translit, String time, String id, int format, String sender, String query) {
        if (!smsServiceEnabled) {
            if (smscDebug) {
                System.out.println("SMS сервис отключен!");
                System.out.println(message);
            }
            return null;
        }

        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1", "mms=1", "mail=1", "call=1", "viber=1", "soc=1"};
        String[] m = {};

        try {
            m = _smsc_send_cmd("send", "cost=3&phones=" + URLEncoder.encode(phones, smscCharset)
                    + "&mes=" + URLEncoder.encode(message, smscCharset)
                    + "&translit=" + translit + "&id=" + id + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, smscCharset))
                    + (time == "" ? "" : "&time=" + URLEncoder.encode(time, smscCharset))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }

        if (m.length > 1) {
            if (smscDebug) {
                if (Integer.parseInt(m[1]) > 0) {
                    System.out.println("Сообщение отправлено успешно. ID: " + m[0] + ", всего SMS: " + m[1] + ", стоимость: " + m[2] + ", баланс: " + m[3]);
                } else {
                    System.out.print("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
                    System.out.println(Integer.parseInt(m[0]) > 0 ? (", ID: " + m[0]) : "");
                }
            }
        } else {
            System.out.println("Не получен ответ от сервера.");
        }

        return m;
    }



    /**
     * Получение стоимости SMS
     *
     * @param phones   - список телефонов через запятую или точку с запятой
     * @param message  - отправляемое сообщение.
     * @param translit - переводить или нет в транслит (1,2 или 0)
     * @param format   - формат сообщения (0 - обычное sms, 1 - flash-sms, 2 - wap-push, 3 - hlr, 4 - bin, 5 - bin-hex, 6 - ping-sms, 7 - mms, 8 - mail, 9 - call, 10 - viber, 11 - soc)
     * @param sender   - имя отправителя (Sender ID)
     * @param query    - строка дополнительных параметров, добавляемая в URL-запрос ("list=79999999999:Ваш пароль: 123\n78888888888:Ваш пароль: 456")
     * @return array(< стоимость >, < количество sms >) либо (0, -<код ошибки>) в случае ошибки
     */
    public String[] get_sms_cost(String phones, String message, int translit, int format, String sender, String query) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1", "mms=1", "mail=1", "call=1", "viber=1", "soc=1"};
        String[] m = {};

        try {
            m = _smsc_send_cmd("send", "cost=1&phones=" + URLEncoder.encode(phones, smscCharset)
                    + "&mes=" + URLEncoder.encode(message, smscCharset)
                    + "&translit=" + translit + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, smscCharset))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }
        // (cost, cnt) или (0, -error)

        if (m.length > 1) {
            if (smscDebug) {
                if (Integer.parseInt(m[1]) > 0)
                    System.out.println("Стоимость рассылки: " + m[0] + ", Всего SMS: " + m[1]);

                else
                    System.out.print("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
            }
        } else
            System.out.println("Не получен ответ от сервера.");

        return m;
    }

    /**
     * Проверка статуса отправленного SMS или HLR-запроса
     *
     * @param id    - ID cообщения
     * @param phone - номер телефона
     * @param all   - дополнительно возвращаются элементы в конце массива:
     *              (<время отправки>, <номер телефона>, <стоимость>, <sender id>, <название статуса>, <текст сообщения>)
     * @return array
     * для отправленного SMS (<статус>, <время изменения>, <код ошибки sms>)
     * для HLR-запроса (<статус>, <время изменения>, <код ошибки sms>, <код страны регистрации>, <код оператора абонента>,
     * <название страны регистрации>, <название оператора абонента>, <название роуминговой страны>, <название роумингового оператор
     * <код IMSI SIM-карты>, <номер сервис-центра>)
     * либо array(0, -<код ошибки>) в случае ошибки
     */
    public String[] get_status(int id, String phone, int all) {
        String[] m = {};
        String tmp;

        try {
            m = _smsc_send_cmd("status", "phone=" + URLEncoder.encode(phone, smscCharset) + "&id=" + id + "&all=" + all);

            if (m.length > 1) {
                if (smscDebug) {
                    if (m[1] != "" && Integer.parseInt(m[1]) >= 0) {
                        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Integer.parseInt(m[1]));
                        System.out.println("Статус SMS = " + m[0]);
                    } else
                        System.out.println("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
                }

                if (all == 1 && m.length > 9 && (m.length < 14 || m[14] != "HLR")) {
                    tmp = _implode(m, ",");
                    m = tmp.split(",", 9);
                }
            } else
                System.out.println("Не получен ответ от сервера.");

        } catch (UnsupportedEncodingException e) {

        }

        return m;
    }

    /**
     * Получениe баланса
     *
     * @return String баланс или пустую строку в случае ошибки
     */
    public String get_balance() {
        String[] m = {};

        m = _smsc_send_cmd("balance", ""); // (balance) или (0, -error)

        if (m.length >= 1) {
            if (smscDebug) {
                if (m.length == 1)
                    System.out.println("Сумма на счете: " + m[0]);
                else
                    System.out.println("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
            }
        } else {
            System.out.println("Не получен ответ от сервера.");
        }
        return m.length == 2 ? "" : m[0];
    }

    /**
     * Формирование и отправка запроса
     *
     * @param cmd - требуемая команда
     * @param arg - дополнительные параметры
     */

    private String[] _smsc_send_cmd(String cmd, String arg) {
        /* String[] m = {}; */
        String ret = ",";

        try {
            String _url = (smscHttps ? "https" : "http") + "://smsc.ru/sys/" + cmd + ".php?login=" + URLEncoder.encode(smscLogin, smscCharset)
                    + "&psw=" + URLEncoder.encode(smscPassword, smscCharset)
                    + "&fmt=1&charset=" + smscCharset + "&" + arg;

            String url = _url;
            int i = 0;
            do {
                if (i++ > 0) {
                    url = _url;
                    url = url.replace("://smsc.ru/", "://www" + (i) + ".smsc.ru/");
                }
                ret = _smsc_read_url(url);
            }
            while (ret == "" && i < 5);
        } catch (UnsupportedEncodingException e) {

        }
        return ret.split(",");
    }

    /**
     * Чтение URL
     *
     * @param url - ID cообщения
     * @return line - ответ сервера
     */
    private String _smsc_read_url(String url) {

        String line = "", real_url = url;
        String[] param = {};
        boolean is_post = (smscPost || url.length() > 2000);

        if (is_post) {
            param = url.split("\\?", 2);
            real_url = param[0];
        }

        try {
            URL u = new URL(real_url);
            InputStream is;

            if (is_post) {
                URLConnection conn = u.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), smscCharset);
                os.write(param[1]);
                os.flush();
                os.close();
                System.out.println("post");
                is = conn.getInputStream();
            } else {
                is = u.openStream();
            }

            InputStreamReader reader = new InputStreamReader(is, smscCharset);

            int ch;
            while ((ch = reader.read()) != -1) {
                line += (char) ch;
            }

            reader.close();
        } catch (MalformedURLException e) { // Неверно урл, протокол...

        } catch (IOException e) {

        }

        return line;
    }

    private static String _implode(String[] ary, String delim) {
        String out = "";

        for (int i = 0; i < ary.length; i++) {
            if (i != 0)
                out += delim;
            out += ary[i];
        }

        return out;
    }
}

// Examples:
/*
		Smsc sd= new Smsc();
		// or
		Smsc sd= new Smsc("login", "password");
		sd.send_sms("79999999999", "Ваш пароль: 123", 1, "", "", 0, "", "");
		sd.get_sms_cost("79999999999", "Вы успешно зарегистрированы!", 0, 0, "", "");
		sd.get_status(sms_id, "79999999999");
		sd.get_balanse();
*/