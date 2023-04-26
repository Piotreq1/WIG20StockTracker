package com.example.listagpwapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String[] companies = {"ALIOR BANK SA", "ALLEGRO.EU SA", "ASSECO POLAND SA",
            "BANK PEKAO SA", "CD PROJEKT SA", "CYFROWY POLSAT SA",
            "DINO POLSKA SA", "GRUPA KĘTY SA", "JASTRZĘBSKA SPÓŁKA WĘGLOWA SA",
            "KGHM POLSKA MIEDŹ SA", "KRUK SA", "LPP SA", "MBANK SA", "ORANGE POLSKA SA",
            "PGE SA","PKN ORLEN SA","PKO BP SA","PZU SA","SANTANDER BANK POLSKA SA", " --- "};
    ArrayAdapter<String> adapter;
    ListView listView;
    Map<String, String> linkMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.list_activity);
        listView = findViewById(R.id.list_items);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, companies);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);


        new DownloadTask().execute("https://www.money.pl/gielda/spolki-gpw/");
    }

    private class DownloadTask extends AsyncTask<String, Void, Elements> {

        @Override
        protected Elements doInBackground(String... urls) {
            String url = urls[0];
            Elements rows = null;
            try {
                Document doc = Jsoup.connect(url).timeout(10 * 1000).get();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String href = link.attr("href");

                    if (href.contains("/gielda/spolki-gpw/")) {
                        String text = link.text();

                        for (String s : companies) {
                            if (text.toUpperCase().contains(s)) {
                                linkMap.put(href, s);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rows;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String txt = adapterView.getItemAtPosition(i).toString();
        for (Map.Entry<String, String> s : linkMap.entrySet()) {
            if (txt.equals(s.getValue())) {
                Intent intent = new Intent(ListActivity.this, CompanyActivity.class);
                intent.putExtra("href", s.getKey());
                intent.putExtra("company", s.getValue());
                startActivity(intent);
            }
        }
    }
}