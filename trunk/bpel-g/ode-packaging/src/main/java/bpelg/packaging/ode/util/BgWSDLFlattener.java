package bpelg.packaging.ode.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ibm.wsdl.extensions.schema.SchemaImpl;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BgWSDLFlattener {

    private Definition _definition;
    private BgSchemaCollection _schemas;
    private Map<QName, Definition> _flattened;
    private boolean _initialized;


    public BgWSDLFlattener(Definition definition) {
        this(definition, null);
    }

    public BgWSDLFlattener(Definition definition, BgSchemaCollection schemas) {
        if (definition == null)
            throw new NullPointerException("Null definition!");
        this._definition = definition;
        this._flattened = new ConcurrentHashMap<>();
        this._schemas = schemas;
    }

    /**
     * Parse the schemas referenced by the definition.
     *
     * @throws Exception if an error occurs
     */
    public void initialize() throws Exception {
        if (!_initialized) {
            if (_schemas == null) {
                this._schemas = new BgSchemaCollection(getUri(_definition.getDocumentBaseURI()));
            }
            parseSchemas(this._definition);
            _initialized = true;
        }
    }

    /**
     * Retrieve a flattened definition for a given port type name.
     *
     * @param portType the port type to create a flat definition for
     * @return a flat definition for the port type
     * @throws Exception if an error occurs
     */
    public Definition getDefinition(QName portType) throws Exception {
        Definition def = _flattened.get(portType);
        if (def == null) {
            def = flattenDefinition(portType);
            _flattened.put(portType, def);
        }
        return def;
    }

    /**
     * @return Returns the definition.
     */
    public Definition getDefinition() {
        return _definition;
    }

    /**
     * @param definition The definition to set.
     */
    public void setDefinition(Definition definition) {
        this._definition = definition;
    }

    /**
     * @return Returns the schemas.
     */
    public BgSchemaCollection getSchemas() throws Exception {
        return _schemas;
    }

    /**
     * @param schemas The schemas to set.
     */
    public void setSchemas(BgSchemaCollection schemas) {
        this._schemas = schemas;
    }

    private Definition flattenDefinition(QName name) throws Exception {
        // Check that schemas have been loaded
        initialize();
        // Create new definition
        Definition flat = WSDLFactory.newInstance().newDefinition();
        flat.setTargetNamespace(name.getNamespaceURI());
        addNamespaces(flat, _definition);
        // Create port type
        PortType defPort = _definition.getPortType(name);
        PortType flatPort = flat.createPortType();
        flatPort.setQName(defPort.getQName());
        flatPort.setUndefined(false);
        // Import all operations and related messages
        for (Object o : defPort.getOperations()) {
            Operation defOper = (Operation) o;
            Operation flatOper = flat.createOperation();
            flatOper.setName(defOper.getName());
            flatOper.setStyle(defOper.getStyle());
            flatOper.setUndefined(false);
            if (defOper.getInput() != null) {
                Input flatInput = flat.createInput();
                flatInput.setName(defOper.getInput().getName());
                if (defOper.getInput().getMessage() != null) {
                    Message flatInputMsg = copyMessage(defOper.getInput().getMessage(), flat);
                    flatInput.setMessage(flatInputMsg);
                    flat.addMessage(flatInputMsg);
                }
                flatOper.setInput(flatInput);
            }
            if (defOper.getOutput() != null) {
                Output flatOutput = flat.createOutput();
                flatOutput.setName(defOper.getOutput().getName());
                if (defOper.getOutput().getMessage() != null) {
                    Message flatOutputMsg = copyMessage(defOper.getOutput().getMessage(), flat);
                    flatOutput.setMessage(flatOutputMsg);
                    flat.addMessage(flatOutputMsg);
                }
                flatOper.setOutput(flatOutput);
            }
            for (Object o1 : defOper.getFaults().values()) {
                Fault defFault = (Fault) o1;
                Fault flatFault = flat.createFault();
                flatFault.setName(defFault.getName());
                if (defFault.getMessage() != null) {
                    Message flatFaultMsg = copyMessage(defFault.getMessage(), flat);
                    flatFault.setMessage(flatFaultMsg);
                    flat.addMessage(flatFaultMsg);
                }
                flatOper.addFault(flatFault);
            }
            flatPort.addOperation(flatOper);
        }

        // Import schemas in definition
        if (_schemas.getSize() > 0) {
            Types types = flat.createTypes();
            for (Object o : _schemas.getSchemas()) {
                Schema imp = new SchemaImpl();
                imp.setElement(((BgSchema) o).getRoot());
                imp.setElementType(new QName("http://www.w3.org/2001/XMLSchema", "schema"));
                types.addExtensibilityElement(imp);
            }
            flat.setTypes(types);
        }

        flat.addPortType(flatPort);
        return flat;
    }

    private void parseSchemas(Definition def) throws Exception {
        if (def.getTypes() != null && def.getTypes().getExtensibilityElements() != null) {
            for (Object o : def.getTypes().getExtensibilityElements()) {
                ExtensibilityElement element = (ExtensibilityElement) o;
                if (element instanceof Schema) {
                    Schema schema = (Schema) element;
                    if (schema.getElement() != null) {
                        _schemas.read(schema.getElement(), getUri(schema.getDocumentBaseURI()));
                    }
                    for (Object o1 : schema.getImports().values()) {
                        Collection imps = (Collection) o1;
                        for (Object imp1 : imps) {
                            SchemaImport imp = (SchemaImport) imp1;
                            _schemas.read(imp.getSchemaLocationURI(), getUri(def.getDocumentBaseURI()));
                        }
                    }
                }
            }
        }
        if (def.getImports() != null) {
            for (Object o : def.getImports().values()) {
                Collection imps = (Collection) o;
                for (Object imp1 : imps) {
                    Import imp = (Import) imp1;
                    parseSchemas(imp.getDefinition());
                }
            }
        }
    }

    private void addNamespaces(Definition flat, Definition def) {
        for (Object o : def.getImports().values()) {
            List defImports = (List) o;
            for (Object defImport1 : defImports) {
                Import defImport = (Import) defImport1;
                addNamespaces(flat, defImport.getDefinition());
            }
        }
        for (Object o : def.getNamespaces().keySet()) {
            String key = (String) o;
            String val = def.getNamespace(key);
            flat.addNamespace(key, val);
        }
    }

    private Message copyMessage(Message defMessage, Definition flat) {
        Message flatMsg = flat.createMessage();
        flatMsg.setUndefined(false);
        if (defMessage.getQName() != null) {
            flatMsg.setQName(new QName(flat.getTargetNamespace(), defMessage.getQName().getLocalPart()));
        }
        for (Object o : defMessage.getParts().values()) {
            Part defPart = (Part) o;
            Part flatPart = flat.createPart();
            flatPart.setName(defPart.getName());
            flatPart.setElementName(defPart.getElementName());
            flatMsg.addPart(flatPart);
        }
        return flatMsg;
    }

    private URI getUri(String str) {
        if (str != null) {
            str = str.replaceAll(" ", "%20");
            return URI.create(str);
        }
        return null;
    }

}
