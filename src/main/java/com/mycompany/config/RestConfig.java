package com.mycompany.config;

import com.mycompany.rest.RemoteAuthResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import com.mycompany.rest.UserResource;

@ApplicationPath("/api")
public class RestConfig extends Application {
@Override
public Set<Class<?>> getClasses() {
    Set<Class<?>> resources = new HashSet<>();
    resources.add(UserResource.class);
    resources.add(RemoteAuthResource.class);  // Προσθήκη του νέου resource
    return resources;
}
}