package com.example.listagpwapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CompanyActivity extends AppCompatActivity {

    TextView title;
    String company, href;

    TextView tvKurs, tvZmiana, tvZmianaProcent, tvOtwarcie, tvMax, tvMin, tvWolumen, tvObrot, tvCzas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.company_activity);

        title = findViewById(R.id.title);
        tvKurs = findViewById(R.id.tvKurs);
        tvZmiana = findViewById(R.id.tvZmiana);
        tvZmianaProcent = findViewById(R.id.tvZmianaProcent);
        tvOtwarcie = findViewById(R.id.tvOtwarcie);
        tvMax = findViewById(R.id.tvMax);
        tvMin = findViewById(R.id.tvMin);
        tvWolumen = findViewById(R.id.tvWolumen);
        tvObrot = findViewById(R.id.tvObrot);
        tvCzas = findViewById(R.id.tvCzas);

        if (getIntent() != null) {
            company = getIntent().getStringExtra("company");
            href = getIntent().getStringExtra("href");
            title.setText(company);
        }

        String symbol = href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf("."));
        new DownloadTask().execute("https://notowania.pb.pl/stocktable/WIG20", symbol);

    }


    private class DownloadTask extends AsyncTask<String, Void, Elements> {

        @Override
        protected Elements doInBackground(String... urls) {
            String url = urls[0];
            String id = urls[1];
            Elements rows = null;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains(id)) {
                        Element trElement = link.parent().parent();
                        rows = trElement.getElementsByTag("td");
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return rows;
        }

        @Override
        protected void onPostExecute(Elements rows) {
            if (rows != null && rows.size() >= 9) {
                tvKurs.setText(rows.get(1).text());
                tvZmianaProcent.setText(rows.get(2).text());
                tvZmiana.setText(rows.get(3).text());
                tvOtwarcie.setText(rows.get(4).text());
                tvMax.setText(rows.get(5).text());
                tvMin.setText(rows.get(6).text());
                tvWolumen.setText(rows.get(7).text());
                tvObrot.setText(rows.get(8).text());
                tvCzas.setText(rows.get(9).text());
            }
        }
    }
}

