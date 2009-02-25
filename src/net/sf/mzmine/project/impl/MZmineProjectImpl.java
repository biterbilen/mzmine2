/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.project.impl;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.mzmine.data.Parameter;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.project.MZmineProject;
import net.sf.mzmine.project.ProjectEvent;

/**
 * This class represents a MZmine project. That includes raw data files,
 * processed raw data files, peak lists, alignment results....
 */
public class MZmineProjectImpl implements MZmineProject {

    private Hashtable<Parameter, Hashtable<String, Object>> projectParametersAndValues;

    private Vector<RawDataFile> dataFiles;
    private Vector<PeakList> peakLists;

    private File projectFile;

    public MZmineProjectImpl() {

        this.dataFiles = new Vector<RawDataFile>();
        this.peakLists = new Vector<PeakList>();
        projectParametersAndValues = new Hashtable<Parameter, Hashtable<String, Object>>();

    }

    public void addParameter(Parameter parameter) {
        if (projectParametersAndValues.containsKey(parameter))
            return;

        Hashtable<String, Object> parameterValues = new Hashtable<String, Object>();
        projectParametersAndValues.put(parameter, parameterValues);

    }

    public void removeParameter(Parameter parameter) {
        projectParametersAndValues.remove(parameter);
    }

    public boolean hasParameter(Parameter parameter) {
        return projectParametersAndValues.containsKey(parameter);
    }

    public Parameter[] getParameters() {
        return projectParametersAndValues.keySet().toArray(new Parameter[0]);
    }

    public void setParameterValue(Parameter parameter, RawDataFile rawDataFile,
            Object value) {
        if (!(hasParameter(parameter)))
            addParameter(parameter);
        Hashtable<String, Object> parameterValues = projectParametersAndValues.get(parameter);
        parameterValues.put(rawDataFile.getName(), value);
    }

    public Object getParameterValue(Parameter parameter, RawDataFile rawDataFile) {
        if (!(hasParameter(parameter)))
            return null;
        Object value = projectParametersAndValues.get(parameter).get(
                rawDataFile.getName());
        if (value == null)
            return parameter.getDefaultValue();
        return value;
    }

    public void addFile(RawDataFile newFile) {
        dataFiles.add(newFile);
        ProjectManagerImpl.getInstance().fireListeners(
                ProjectEvent.DATAFILE_ADDED);
    }

    public void removeFile(RawDataFile file) {
        dataFiles.remove(file);
        file.close();
        ProjectManagerImpl.getInstance().fireListeners(
                ProjectEvent.DATAFILE_REMOVED);
    }

    public RawDataFile[] getDataFiles() {
        return dataFiles.toArray(new RawDataFile[0]);
    }

    public void addPeakList(PeakList peakList) {
        peakLists.add(peakList);
        ProjectManagerImpl.getInstance().fireListeners(
                ProjectEvent.PEAKLIST_ADDED);
    }

    public void removePeakList(PeakList peakList) {
        peakLists.remove(peakList);
        ProjectManagerImpl.getInstance().fireListeners(
                ProjectEvent.PEAKLIST_REMOVED);
    }

    public PeakList[] getPeakLists() {
        return peakLists.toArray(new PeakList[0]);
    }

    public PeakList[] getPeakLists(RawDataFile file) {
        Vector<PeakList> result = new Vector<PeakList>();
        for (PeakList peakList : peakLists) {
            if (peakList.hasRawDataFile(file))
                result.add(peakList);
        }
        return result.toArray(new PeakList[0]);
    }

    public File getProjectFile() {
        return projectFile;
    }

    void setProjectFile(File file) {
        this.projectFile = file;
        ProjectManagerImpl.getInstance().fireListeners(
                ProjectEvent.NAME_CHANGED);
    }

    public String toString() {
        if (projectFile == null)
            return "New project";
        String projectName = projectFile.getName();
        if (projectName.endsWith(".mzmine")) {
            projectName = projectName.substring(0, projectName.length() - 7);
        }
        return projectName;
    }

}