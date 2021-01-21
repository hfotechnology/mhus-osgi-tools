/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.api.util;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import de.mhus.lib.core.MApi;
import de.mhus.osgi.api.MOsgi;

public class OsgiBundleClassLoader extends ClassLoader {

    private String loadedFrom;

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        loadedFrom = FrameworkUtil.getBundle(MApi.class).getSymbolicName();
        Bundle myBundle = FrameworkUtil.getBundle(MApi.class);
        BundleContext context = myBundle.getBundleContext();
        if (context == null) {
            context = MOsgi.getBundleContext();
            loadedFrom = "MOsgi";
        }
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            try {
                Class<?> clazz = bundle.loadClass(name);
                if (clazz != null) {
                    loadedFrom = bundle.getSymbolicName();
                    return clazz;
                }
            } catch (Throwable t) {
            }
        }
        return super.loadClass(name, resolve);
    }

    public Map<String, Class<?>> loadAllClasses(String name) {
        HashMap<String, Class<?>> out = new HashMap<>();
        loadedFrom = FrameworkUtil.getBundle(OsgiBundleClassLoader.class).getSymbolicName();
        for (Bundle bundle :
                FrameworkUtil.getBundle(OsgiBundleClassLoader.class)
                        .getBundleContext()
                        .getBundles()) {
            try {
                Class<?> clazz = bundle.loadClass(name);
                if (clazz != null) {
                    out.put(bundle.getSymbolicName(), clazz);
                }
            } catch (Throwable t) {
            }
        }
        return out;
    }

    public String getLoadBundle() {
        return loadedFrom;
    }
}
