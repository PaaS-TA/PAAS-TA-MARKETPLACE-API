package org.openpaas.paasta.marketplace.api.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-20
 */
@Slf4j
public class CommonUtils {

    public Map<String, Object> convertYamlToJson(Object object) {
        Yaml yaml = new Yaml();

        Reader yamlFile = null;

        try {
            yamlFile = new FileReader("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, Object> yamlMaps = yaml.load(yamlFile);

        System.out.println(yamlMaps.toString());

        return yamlMaps;
    }





}