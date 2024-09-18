package de.jxnxsdev.craftNet.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Universe {
    @Getter @Setter
    int id;
    @Getter @Setter
    List<Channel> channels;
}
