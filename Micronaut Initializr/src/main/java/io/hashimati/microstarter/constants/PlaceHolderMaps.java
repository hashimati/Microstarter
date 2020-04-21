package io.hashimati.microstarter.constants;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

@Data
@ToString
@NoArgsConstructor
public class PlaceHolderMaps
{
    private String file;
    private HashMap<String, String> varaibelMethod = new HashMap<String, String>();
}
