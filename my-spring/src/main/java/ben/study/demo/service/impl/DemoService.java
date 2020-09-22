package ben.study.demo.service.impl;

import ben.study.demo.service.IDemoService;

public class DemoService implements IDemoService {
    public String get(String name) {
        return "My name is " + name + ", from service";
    }
}
