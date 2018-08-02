package com.polidea.cockpit.core.mapper

import com.polidea.cockpit.core.CockpitParam
import com.polidea.cockpit.core.exception.CockpitParseException
import com.polidea.cockpit.core.type.CockpitAction
import com.polidea.cockpit.core.type.CockpitColor
import com.polidea.cockpit.core.type.CockpitListType

internal class YamlToParamMapper {

    fun toListOfParams(yamlMap: Map<String, Any>): List<CockpitParam<Any>> {
        return yamlMap.map {
            val value = it.value
            val param = when (value) {
                is Map<*, *> -> fromExtendedYamlFormat(it.key, value) // new extended yaml format
                else -> fromSimpleYamlFormat(it.key, value)           // previous simple yaml format
            }
            param
        }
    }

    private fun fromSimpleYamlFormat(paramName: String, paramValue: Any): CockpitParam<Any> {
        val paramValue = when (paramValue) {
            is List<*> -> CockpitListType(ArrayList<Any>(paramValue), 0)
            else -> paramValue
        }
        return CockpitParam(paramName, paramValue)
    }

    private fun fromExtendedYamlFormat(paramName: String, valueMap: Map<*, *>): CockpitParam<Any> {
        val type = YamlParamType.forValue(valueMap[MapperConsts.KEY_TYPE] as String?)
        val value = when (type) {
            YamlParamType.ACTION -> cockpitAction(valueMap)
            YamlParamType.LIST -> cockpitListType(paramName, valueMap)
            YamlParamType.COLOR -> cockpitColor(paramName, valueMap)
            YamlParamType.DEFAULT -> valueMap[MapperConsts.KEY_VALUE] as Any
        }
        val description = valueMap[MapperConsts.KEY_DESCRIPTION] as String?
        val group = valueMap[MapperConsts.KEY_GROUP] as String?
        return CockpitParam(paramName, value, description, group)
    }

    private fun cockpitAction(valueMap: Map<*, *>) =
            CockpitAction(valueMap[MapperConsts.KEY_ACTION_BUTTON_TEXT] as? String)

    private fun cockpitListType(paramName: String, valueMap: Map<*, *>): CockpitListType<Any> {
        val values = valueMap[MapperConsts.KEY_LIST_VALUES] as? List<*>
                ?: throw CockpitParseException("$paramName parameter must contain list of elements in `${MapperConsts.KEY_LIST_VALUES}` field")
        val selectedIndex = (valueMap[MapperConsts.KEY_LIST_SELECTION_INDEX] as Int?) ?: 0
        return CockpitListType(ArrayList<Any>(values), selectedIndex)
    }

    private fun cockpitColor(paramName: String, valueMap: Map<*, *>): CockpitColor {
        val colorValue = valueMap[MapperConsts.KEY_VALUE] as? String
                ?: throw CockpitParseException("$paramName must contain String value param")
        try {
            return CockpitColor(colorValue)
        } catch (e: IllegalArgumentException) {
            throw CockpitParseException("Invalid color format for `$paramName` param. Supported formats are: #RRGGBB and #AARRGGBB.")
        }
    }
}