package com.miapp.tv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogFragment extends BrowseSupportFragment {

    // Cambia esta URL por la de tu catalogo.json subido en GitHub (raw)
    private static final String JSON_URL =
            "https://raw.githubusercontent.com/TU_USUARIO/TU_REPO/main/catalogo.json";

    private final ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle("Mi App TV");
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(0xFF1A1A1A);
        setAdapter(rowsAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                       RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof Movie) {
                    Movie movie = (Movie) item;
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("titulo", movie.titulo);
                    intent.putExtra("url_video", movie.url_video);
                    startActivity(intent);
                }
            }
        });

        cargarCatalogoRemoto();
    }

    private void cargarCatalogoRemoto() {
        executor.execute(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) sb.append(linea);
                reader.close();

                parsearJson(sb.toString());
            } catch (Exception e) {
                Log.e("CatalogFragment", "Error cargando JSON", e);
            }
        });
    }

    private void parsearJson(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        JSONArray categorias = root.getJSONArray("categorias");

        for (int i = 0; i < categorias.length(); i++) {
            JSONObject categoria = categorias.getJSONObject(i);
            String nombreCategoria = categoria.getString("nombre");
            JSONArray peliculasJson = categoria.getJSONArray("peliculas");

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for (int j = 0; j < peliculasJson.length(); j++) {
                JSONObject p = peliculasJson.getJSONObject(j);
                Movie movie = new Movie(
                        p.getString("id"),
                        p.getString("titulo"),
                        p.optString("descripcion", ""),
                        p.getString("portada"),
                        p.getString("url_video")
                );
                listRowAdapter.add(movie);
            }

            HeaderItem header = new HeaderItem(i, nombreCategoria);
            ListRow row = new ListRow(header, listRowAdapter);

            mainHandler.post(() -> rowsAdapter.add(row));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
