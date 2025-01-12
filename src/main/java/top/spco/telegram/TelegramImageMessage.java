package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import top.spco.api.Image;
import top.spco.api.message.Message;

import java.io.File;
import java.util.ArrayList;

public class TelegramImageMessage extends Image<InputFile> {
    TelegramImageMessage(InputFile image) {
        super(image);
    }

    TelegramImageMessage(File image) {
        this(new InputFile(image));
    }

    @Override
    public String getImageId() {
        return wrapped().getAttachName();
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
    public String toMessageContext() {
        return "1 Photo";
    }

    @Override
    public Message<?> toMessage() {
        org.telegram.telegrambots.meta.api.objects.message.Message message = new org.telegram.telegrambots.meta.api.objects.message.Message();
        ArrayList<PhotoSize> files = new ArrayList<>();
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId("filePath://" + wrapped().getNewMediaFile().getAbsolutePath());
        files.set(0, photoSize);
        message.setPhoto(files);
        return new TelegramMessage(message);
    }

    @Override
    public String serialize() {
        return "";
    }
}
