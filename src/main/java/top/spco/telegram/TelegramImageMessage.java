package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import top.spco.api.message.Image;
import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

class TelegramImageMessage extends Image {
    private final InputFile inputFile;

    TelegramImageMessage(InputFile image) {
        this.inputFile = image;
    }

    TelegramImageMessage(File image) {
        this.inputFile = new InputFile(image);
    }

    @Override
    public String getImageId() {
        return inputFile.getAttachName();
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public URL getUrl() {
        try {
            return inputFile.getNewMediaFile().toURI().toURL();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toMessageContext() {
        return "1 Photo";
    }

    @Override
    public MessageChain toMessageChain() {
        org.telegram.telegrambots.meta.api.objects.message.Message message = new org.telegram.telegrambots.meta.api.objects.message.Message();
        ArrayList<PhotoSize> files = new ArrayList<>();
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId("filePath://" + inputFile.getNewMediaFile().getAbsolutePath());
        files.set(0, photoSize);
        message.setPhoto(files);
        // TODO: FIX THIS
        return null;
        //return new MessageChain().append(new TelegramImageMessage(files));
    }
}
