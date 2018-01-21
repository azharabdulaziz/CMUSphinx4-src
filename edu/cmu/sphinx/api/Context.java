/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package edu.cmu.sphinx.api;

import static edu.cmu.sphinx.util.props.ConfigurationManagerUtils.resourceToURL;
import static edu.cmu.sphinx.util.props.ConfigurationManagerUtils.setProperty;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank2;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader;
import edu.cmu.sphinx.linguist.language.ngram.LanguageModel;
import edu.cmu.sphinx.util.TimeFrame;
import edu.cmu.sphinx.util.Utilities;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertySheet;


/**
 * Helps to tweak configuration without touching XML-file directly.
 */
public class Context {

    private final ConfigurationManager configurationManager;

    /**
     * Constructs builder that uses default XML configuration.
     * @param config configuration
     * @throws MalformedURLException if failed to load configuration file
     */
    public Context(Configuration config)
        throws IOException, MalformedURLException
    {
        this("src/edu/cmu/sphinx/api/default.config.xml", config);
    }

    /**
     * Constructs builder using user-supplied XML configuration.
     *
     * @param  path path to XML-resource with configuration
     * @param  config configuration
     * @throws MalformedURLException if failed to load configuration file
     * @throws IOException           if failed to load configuration file
     */
    public Context(String path, Configuration config)
        throws IOException, MalformedURLException
    {
        configurationManager = new ConfigurationManager(resourceToURL(path));

        setAcousticModel(config.getAcousticModelPath());
        setDictionary(config.getDictionaryPath());

        if (null != config.getGrammarPath() && config.getUseGrammar())
            setGrammar(config.getGrammarPath(), config.getGrammarName());
        if (null != config.getLanguageModelPath() && !config.getUseGrammar())
            setLanguageModel(config.getLanguageModelPath());

        setSampleRate(config.getSampleRate());

        // Force ConfigurationManager to build the whole graph
        // in order to enable instance lookup by class.
        configurationManager.lookup("recognizer");
    }

    /**
     * Sets acoustic model location.
     *
     * It also reads feat.params which should be located at the root of
     * acoustic model and sets corresponding parameters of
     * {@link MelFrequencyFilterBank2} instance.
     *
     * @param  path path to directory with acoustic model files
     *
     * @throws IOException if failed to read feat.params
     */
    public void setAcousticModel(String path) throws IOException {
        setLocalProperty("acousticModelLoader->location", path);
        setLocalProperty("dictionary->fillerPath", Utilities.pathJoin(path, "noisedict"));
    }

    /**
     * Sets dictionary.
     *
     * @param path path to directory with dictionary files
     */
    public void setDictionary(String path) {
        setLocalProperty("dictionary->dictionaryPath", path);
    }

    /**
     * Sets sampleRate.
     *
     * @param sampleRate sample rate of the input stream.
     */
    public void setSampleRate(int sampleRate) {
        setLocalProperty("dataSource->sampleRate", Integer.toString(sampleRate));
    }

    /**
     * Sets path to the grammar files.
     *
     * Enables static grammar and disables probabilistic language model.
     * JSGF and GrXML formats are supported.
     *
     * @param path path to the grammar files
     * @param name name of the main grammar to use
     * @see        Context#setLanguageModel(String)
     */
    public void setGrammar(String path, String name) {
        // TODO: use a single param of type File, cache directory part
        if (name.endsWith(".grxml")) {
            setLocalProperty("grXmlGrammar->grammarLocation", path + name);
            setLocalProperty("flatLinguist->grammar", "grXmlGrammar");
        } else {
            setLocalProperty("jsgfGrammar->grammarLocation", path);
            setLocalProperty("jsgfGrammar->grammarName", name);
            setLocalProperty("flatLinguist->grammar", "jsgfGrammar");
        }
        setLocalProperty("decoder->searchManager", "simpleSearchManager");
    }

    /**
     * Sets path to the language model.
     *
     * Enables probabilistic language model and disables static grammar.
     * Currently it supports ".lm", ".dmp" and ".bin" file formats.
     *
     * @param  path path to the language model file
     * @see   Context#setGrammar(String, String)
     *
     * @throws IllegalArgumentException if path ends with unsupported extension
     */
    public void setLanguageModel(String path) {
        if (path.endsWith(".lm")) {
            setLocalProperty("simpleNGramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "simpleNGramModel");
        } else if (path.endsWith(".dmp")) {
            setLocalProperty("largeTrigramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "largeTrigramModel");
        } else if (path.endsWith(".bin")) {
            setLocalProperty("trieNgramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "trieNgramModel");
        } else {
            throw new IllegalArgumentException(
                "Unknown format extension: " + path);
        }
        //search manager for LVCSR is set by deafult
    }
    
    
    public String getLanguageModel() {
    	String lmPath = getLocalProperty("largeTrigramModel->location");
        /*if (path.endsWith(".lm")) {
            setLocalProperty("simpleNGramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "simpleNGramModel");
        } else if (path.endsWith(".dmp")) {
            setLocalProperty("largeTrigramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "largeTrigramModel");
        } else if (path.endsWith(".bin")) {
            setLocalProperty("trieNgramModel->location", path);
            setLocalProperty(
                "lexTreeLinguist->languageModel", "trieNgramModel");
        } else {
            throw new IllegalArgumentException(
                "Unknown format extension: " + path);
        }*/
        //search manager for LVCSR is set by deafult
        return lmPath;
    }
    

private String getLocalProperty(String string) {
		
		String properyValue  = getProperty(string);
		return properyValue;
	}

/**
 * 
 * @param string
 * @return
 * @author Azhar Sabah Abdulaziz
 * 
 */

/**
 * Attempts to get the value of a component-property. If the property-name is ambiguous  with respect to
 * the given <code>ConfiguratioManager</code> an extended syntax (componentName-&gt;propName) can be used to access the
 * property.
 * <p>
 * @param cm
 * @param propName
 * @param propValue
 * @author Azhar Sabah Abdulaziz
 * @since 2018
 */
private String getProperty(String propName) {
    //assert propValue != null;
	
    Map<String, List<PropertySheet>> allProps = listAllsPropNames(configurationManager);
    Set<String> configurableNames = configurationManager.getComponentNames();

    if (!allProps.containsKey(propName) && !propName.contains("->") && !configurableNames.contains(propName))
        throw new RuntimeException("No property '" + propName + "' in configuration '" + configurationManager.getConfigURL() + "'!");

    // if a configurable-class should be modified
   /* if (configurableNames.contains(propName)) {
        try {
            final Class<? extends Configurable> confClass = Class.forName(propValue).asSubclass(Configurable.class);
            ConfigurationManagerUtils.setClass(cm.getPropertySheet(propName), confClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return;
    }

    if (!propName.contains("->") && allProps.get(propName).size() > 1) {
        throw new RuntimeException("Property-name '" + propName + "' is ambiguous with respect to configuration '"
                + cm.getConfigURL() + "'. Use 'componentName->propName' to disambiguate your request.");
    }
*/
    String componentName;

    // if disambiguation syntax is used find the correct PS first
    if (propName.contains("->")) {
        String[] splitProp = propName.split("->");
        componentName = splitProp[0];
        propName = splitProp[1];
    } else {
        componentName = allProps.get(propName).get(0).getInstanceName();
    }

    String PropertyValue = getPropValue(componentName, propName);
    return PropertyValue;
}

/**
 * 
 * @param cm
 * @param componentName
 * @param propName
 * @return
 * 
 * @author Azhar Sabhah Abdulaziz
 * @since 2018
 */
private String getPropValue(String componentName, String propName) {

	// now get the property
    PropertySheet ps = configurationManager.getPropertySheet(componentName);
    if (ps == null)
        throw new RuntimeException("Component '" + propName + "' is not registered to this system configuration '");
    String propValue = null;
    switch (ps.getType(propName)) {
        case BOOLEAN:
        	
        		propValue = ps.getBoolean(propName).toString();
            //ps.setBoolean(propName, Boolean.valueOf(propValue));
            break;
        case DOUBLE:
        		propValue = Double.toString(ps.getDouble(propName)); 
            //ps.setDouble(propName, new Double(propValue));
            break;
        case INT:
        		propValue = Integer.toString(ps.getInt(propName));
            //ps.setInt(propName, new Integer(propValue));
            break;
        case STRING:
        		propValue = ps.getString(propName);
            //ps.setString(propName, propValue);
            break;
        case COMPONENT:
        		propValue = ps.getComponent(propName).toString();
            //ps.setComponent(propName, propValue, null);
            break;
        /*case COMPONENT_LIST:
        	
            List<String> compNames = new ArrayList<String>();
            for (String component : propValue.split(";")) {
                compNames.add(component.trim());
            }

            ps.setComponentList(propName, compNames, null);
            break;
*/            
            default:
            throw new RuntimeException("unknown property-type");
    }
    
    return propValue;
	
}



/**
 * Returns a map of all component-properties of this config-manager (including their associated property-sheets.
 * 
 * @param cm configuration manager
 * @return map with properties
 */
private static Map<String, List<PropertySheet>> listAllsPropNames(ConfigurationManager cm) {
    Map<String, List<PropertySheet>> allProps = new HashMap<String, List<PropertySheet>>();

    for (String configName : cm.getComponentNames()) {
        PropertySheet ps = cm.getPropertySheet(configName);

        for (String propName : ps.getRegisteredProperties()) {
            if (!allProps.containsKey(propName))
                allProps.put(propName, new ArrayList<PropertySheet>());

            allProps.get(propName).add(ps);
        }
    }

    return allProps;
}

    /*public String getLocalProperty(String name) {
		// TODO Auto-generated method stub
    configurationManager.getPropertySheet();
    	getProperty(configurationManager, name);
		return null;
	}
*/
	public void setSpeechSource(InputStream stream, TimeFrame timeFrame) {
        getInstance(StreamDataSource.class).setInputStream(stream, timeFrame);
        setLocalProperty("trivialScorer->frontend", "liveFrontEnd");
    }

    /**
     * Sets byte stream as the speech source.
     *
     * @param  stream stream to process
     */
    public void setSpeechSource(InputStream stream) {
        getInstance(StreamDataSource.class).setInputStream(stream);
        setLocalProperty("trivialScorer->frontend", "liveFrontEnd");
    }

    /**
     * Sets property within a "component" tag in configuration.
     *
     * Use this method to alter "value" property of a "property" tag inside a
     * "component" tag of the XML configuration.
     *
     * @param  name  property name
     * @param  value property value
     * @see          Context#setGlobalProperty(String, Object)
     */
    public void setLocalProperty(String name, Object value) {
        setProperty(configurationManager, name, value.toString());
    }

    
    /**
     * Sets property of a top-level "property" tag.
     *
     * Use this method to alter "value" property of a "property" tag whose
     * parent is the root tag "config" of the XML configuration.
     *
     * @param  name  property name
     * @param  value property value
     * @see          Context#setLocalProperty(String, Object)
     */
    public void setGlobalProperty(String name, Object value) {
        configurationManager.setGlobalProperty(name, value.toString());
    }

    /**
     * Returns instance of the XML configuration by its class.
     *
     * @param  clazz class to look up
     * @param  <C> generic
     * @return instance of the specified class or null
     */
    public <C extends Configurable> C getInstance(Class<C> clazz) {
        return configurationManager.lookup(clazz);
    }
    
    /**
     * Returns the Loader object used for loading the acoustic model.
     * 
     * @return the loader  object
     */
    public Loader getLoader(){
    	return (Loader) configurationManager.lookup("acousticModelLoader");
    }
}
