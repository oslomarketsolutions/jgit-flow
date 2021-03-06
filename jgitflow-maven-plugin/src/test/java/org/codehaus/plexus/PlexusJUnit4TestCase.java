package org.codehaus.plexus;

/*-
 * #%L
 * JGitFlow :: Maven Plugin
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.plexus.context.Context;
import org.junit.After;
import org.junit.Before;


public abstract class PlexusJUnit4TestCase
{
    protected PlexusContainer container;

    protected String basedir;

    private static String basedirPath;

    @Before
    public void setUp() throws Exception
    {
        InputStream configuration = null;

        try
        {
            configuration = getCustomConfiguration();

            if (configuration == null)
            {
                configuration = getConfiguration();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error with configuration:");

            System.out.println("configuration = " + configuration);

            throw e;
        }

        basedir = getBasedir();

        container = createContainerInstance();

        container.addContextValue("basedir", getBasedir());

        // this method was deprecated
        customizeContext();

        customizeContext(getContext());

        boolean hasPlexusHome = getContext().contains("plexus.home");

        if (!hasPlexusHome)
        {
            File f = getTestFile("target/plexus-home");

            if (!f.isDirectory())
            {
                f.mkdir();
            }

            getContext().put("plexus.home", f.getAbsolutePath());
        }

        if (configuration != null)
        {
            container.setConfigurationResource(new InputStreamReader(configuration));
        }

        container.initialize();

        container.start();
    }

    @After
    public void tearDown() throws Exception
    {
        container.dispose();

        container = null;
    }

    protected PlexusContainer createContainerInstance()
    {
        return new DefaultPlexusContainer();
    }

    private Context getContext()
    {
        return container.getContext();
    }

    //!!! this should probably take a context as a parameter so that the
    //    user is not forced to do getContainer().addContextValue(..)
    //    this would require a change to PlexusContainer in order to get
    //    hold of the context ...
    // @deprecated use void customizeContext( Context context )
    protected void customizeContext() throws Exception
    {
    }


    protected void customizeContext(Context context) throws Exception
    {
    }


    protected InputStream getCustomConfiguration() throws Exception
    {
        return null;
    }

    protected PlexusContainer getContainer()
    {
        return container;
    }

    protected InputStream getConfiguration() throws Exception
    {
        return getConfiguration(null);
    }

    protected InputStream getConfiguration(String subname) throws Exception
    {
        String className = getClass().getName();

        String base = className.substring(className.lastIndexOf(".") + 1);

        String config = null;

        if (subname == null || subname.equals(""))
        {
            config = base + ".xml";
        }
        else
        {
            config = base + "-" + subname + ".xml";
        }

        InputStream configStream = getResourceAsStream(config);

        return configStream;
    }

    protected InputStream getResourceAsStream(String resource)
    {
        return getClass().getResourceAsStream(resource);
    }

    protected ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    // ----------------------------------------------------------------------
    // Container access
    // ----------------------------------------------------------------------

    protected Object lookup(String componentKey) throws Exception
    {
        return getContainer().lookup(componentKey);
    }

    protected Object lookup(String role, String id) throws Exception
    {
        return getContainer().lookup(role, id);
    }

    protected void release(Object component) throws Exception
    {
        getContainer().release(component);
    }

    // ----------------------------------------------------------------------
    // Helper methods for sub classes
    // ----------------------------------------------------------------------

    public static File getTestFile(String path)
    {
        return new File(getBasedir(), path);
    }

    public static File getTestFile(String basedir, String path)
    {
        File basedirFile = new File(basedir);

        if (!basedirFile.isAbsolute())
        {
            basedirFile = getTestFile(basedir);
        }

        return new File(basedirFile, path);
    }

    public static String getTestPath(String path)
    {
        return getTestFile(path).getAbsolutePath();
    }

    public static String getTestPath(String basedir, String path)
    {
        return getTestFile(basedir, path).getAbsolutePath();
    }

    public static String getBasedir()
    {
        if (basedirPath != null)
        {
            return basedirPath;
        }

        basedirPath = System.getProperty("basedir");

        if (basedirPath == null)
        {
            basedirPath = new File("").getAbsolutePath();
        }

        return basedirPath;
    }

}
