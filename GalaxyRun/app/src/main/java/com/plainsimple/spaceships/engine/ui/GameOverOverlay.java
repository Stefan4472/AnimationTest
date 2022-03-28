package com.plainsimple.spaceships.engine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.engine.draw.DrawText;
import com.plainsimple.spaceships.helper.Rectangle;
import com.plainsimple.spaceships.util.ProtectedQueue;

// TODO: needs a lot of cleanup
public class GameOverOverlay extends UIElement {
    // As percent of GAME size
    private final static double WIDTH_PCT = 0.6;
    private final static double HEIGHT_PCT = 0.6;
    private final static double TITLE_SIZE_PCT = 0.2;
    // As percent of OVERLAY size
    private final static double TITLE_MARGIN_BOTTOM_PCT = 0.2;
    private final static double PLAY_BUTTON_WIDTH_PCT = 0.6;
    private final static double PLAY_BUTTON_HEIGHT_PCT = 0.3;
    private final static double PLAY_BUTTON_TEXT_PCT = 0.2;

    // Bounds of the "Play Again" button
    private final Rectangle playAgainBounds;

    public GameOverOverlay(GameContext gameContext) {
        super(gameContext, calcLayout(gameContext));
        double playAgainVMargin = bounds.getHeight() * TITLE_MARGIN_BOTTOM_PCT;
        double playAgainWidth = bounds.getWidth() * PLAY_BUTTON_WIDTH_PCT;
        double playAgainHeight = bounds.getHeight() * PLAY_BUTTON_HEIGHT_PCT;
        playAgainBounds = new Rectangle(
                bounds.getX() + (bounds.getWidth() - playAgainWidth) / 2,
                bounds.getY() + playAgainVMargin + gameContext.gameHeightPx * TITLE_SIZE_PCT,
                playAgainWidth,
                playAgainHeight
        );
        isTouchable = false;
        isVisible = false;
    }

    private static Rectangle calcLayout(GameContext gameContext) {
        double width = gameContext.gameWidthPx * WIDTH_PCT;
        double height = gameContext.gameHeightPx * HEIGHT_PCT;
        return new Rectangle(
                (gameContext.gameWidthPx - width) / 2,
                (gameContext.gameHeightPx - height) / 2,
                width,
                height
        );
    }

    public void show() {
        isTouchable = true;
        isVisible = true;
    }

    public void hide() {
        isTouchable = false;
        isVisible = false;
    }

    @Override
    public void update(UpdateContext updateContext) {

    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawParams) {
        DrawRect box = new DrawRect(Color.BLACK, Paint.Style.FILL, 0);
        box.setBounds(bounds);
        drawParams.push(box);

        // https://stackoverflow.com/a/26975371
        Paint paint = new Paint();
        Rect textBounds = new Rect();
//        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize((int) (gameContext.gameHeightPx * TITLE_SIZE_PCT));
        String text = "Game Over";
        paint.getTextBounds(text, 0, text.length(), textBounds);
        int text_width =  textBounds.width();
        int text_height =  textBounds.height();
        drawParams.push(new DrawText(
                "Game Over",
                (float) (bounds.getX() + (bounds.getWidth() - text_width) / 2),
                (float) (bounds.getY() + text_height + gameContext.gameHeightPx * 0.05),
                Color.YELLOW,
                (int) (gameContext.gameHeightPx * TITLE_SIZE_PCT)
        ));

        DrawRect buttonFill = new DrawRect(Color.GRAY, Paint.Style.FILL, 0);
        buttonFill.setBounds(playAgainBounds);
        drawParams.push(buttonFill);

        Paint paint2 = new Paint();
        Rect textBounds2 = new Rect();
//        paint.setTypeface(Typeface.DEFAULT);
        paint2.setTextSize((int) (bounds.getHeight() * PLAY_BUTTON_TEXT_PCT));
        String text2 = "Play Again";
        paint2.getTextBounds(text2, 0, text2.length(), textBounds2);
        int text_width2 =  textBounds2.width();
        int text_height2 =  textBounds2.height();
        drawParams.push(new DrawText(
                "Play Again",
                (float) (playAgainBounds.getX() + (playAgainBounds.getWidth() - text_width2) / 2),
                (float) (playAgainBounds.getY() + text_height2),
                Color.YELLOW,
                (int) (bounds.getHeight() * PLAY_BUTTON_TEXT_PCT)
        ));
    }

    @Override
    public void onTouchEnter(float x, float y) {

    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchLeave(float x, float y) {
        if (playAgainBounds.isInBounds(x, y)) {
            createdInput.add(UIInputId.RESTART);
        }
    }
}
