package com.customannotationbug.entities;

import com.customannotationbug.AppConfig;
import com.customannotationbug.annotation.EventRoot;

@EventRoot(collection = AppConfig.COLLECTION_NAME)
public class UserEvent {
}
