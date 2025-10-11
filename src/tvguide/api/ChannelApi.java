package tvguide.api;

import tvguide.model.Channel;
import java.util.List;

public interface ChannelApi {
    Channel addChannel(String name);
    void deleteChannel(long id);
    Channel updateChannel(long id, String newName);
    List<Channel> listChannels();
}
