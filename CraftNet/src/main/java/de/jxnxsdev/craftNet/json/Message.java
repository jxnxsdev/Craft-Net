package de.jxnxsdev.craftNet.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Message {
    @Getter @Setter
    String type;
    @Getter @Setter
    List<Universe> data;
}
