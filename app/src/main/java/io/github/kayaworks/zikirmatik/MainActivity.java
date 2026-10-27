package io.github.kayaworks.zikirmatik;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/** A simple tally counter (zikirmatik) with an optional target. */
public class MainActivity extends Activity {

    private static final String PREFS = "counter";
    private static final String KEY_COUNT = "count";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_TARGET = "target";
    private static final int[] TARGETS = {0, 33, 99, 100, 1000};

    private SharedPreferences prefs;
    private int count;
    private long total;
    private int target;

    private TextView countView;
    private TextView targetView;
    private TextView totalView;
    private Button targetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // targetSdk 35+ draws edge-to-edge, so pad the root by the system bars.
        View root = findViewById(R.id.root);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            } else {
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            }
            return insets;
        });

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        count = prefs.getInt(KEY_COUNT, 0);
        total = prefs.getLong(KEY_TOTAL, 0);
        target = prefs.getInt(KEY_TARGET, 33);

        countView = findViewById(R.id.count);
        targetView = findViewById(R.id.target_info);
        totalView = findViewById(R.id.total);
        targetButton = findViewById(R.id.btn_target);

        findViewById(R.id.btn_tap).setOnClickListener(this::onTap);
        findViewById(R.id.btn_minus).setOnClickListener(v -> {
            if (count > 0) {
                count--;
                total = Math.max(0, total - 1);
                save();
            }
        });
        findViewById(R.id.btn_reset).setOnClickListener(v -> confirmReset());
        targetButton.setOnClickListener(v -> chooseTarget());

        render();
    }

    private void onTap(View v) {
        count++;
        total++;
        if (target > 0 && count % target == 0) {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        } else {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        save();
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_title)
                .setMessage(R.string.reset_message)
                .setPositiveButton(R.string.reset, (d, w) -> {
                    count = 0;
                    save();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void chooseTarget() {
        String[] labels = new String[TARGETS.length + 1];
        for (int i = 0; i < TARGETS.length; i++) {
            labels[i] = TARGETS[i] == 0 ? getString(R.string.no_target) : String.valueOf(TARGETS[i]);
        }
        labels[TARGETS.length] = getString(R.string.custom_target);
        new AlertDialog.Builder(this)
                .setTitle(R.string.target)
                .setItems(labels, (d, which) -> {
                    if (which < TARGETS.length) {
                        target = TARGETS[which];
                        save();
                    } else {
                        askCustomTarget();
                    }
                })
                .show();
    }

    private void askCustomTarget() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(R.string.custom_target_hint);
        new AlertDialog.Builder(this)
                .setTitle(R.string.custom_target)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    try {
                        int value = Integer.parseInt(input.getText().toString().trim());
                        if (value > 0 && value <= 1_000_000) {
                            target = value;
                            save();
                        }
                    } catch (NumberFormatException ignored) {
                        // Leave the target unchanged.
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void save() {
        prefs.edit()
                .putInt(KEY_COUNT, count)
                .putLong(KEY_TOTAL, total)
                .putInt(KEY_TARGET, target)
                .apply();
        render();
    }

    private void render() {
        countView.setText(String.valueOf(count));
        totalView.setText(getString(R.string.total_format, total));
        if (target > 0) {
            int rounds = count / target;
            int inRound = count % target;
            targetView.setText(getString(R.string.target_format, inRound, target, rounds));
            targetButton.setText(getString(R.string.target_button, target));
        } else {
            targetView.setText("");
            targetButton.setText(R.string.target);
        }
    }
}
