using Assets.Scripts.Simulation.Towers.Weapons;
using Harmony;
using MelonLoader;
using NKHook6;
using NKHook6.Api;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Reflection;

namespace Assembly_Extractor {
    public class Main : MelonMod {

        public override void OnApplicationStart() {
            base.OnApplicationStart();
            Logger.Log("Assembly Extractor loaded.");

            Dictionary<string, string> propertyMap = new Dictionary<string, string>();

            Logger.Log("Loading assembly...");
            Assembly btdAssembly = typeof(Weapon).GetTypeInfo().Assembly;
            IEnumerable<Type> types = Util.GetLoadableTypes(btdAssembly);
            List<string> typeJsons = new List<string>();
            foreach (var type in types) {
                if (true) { // type.Namespace != null && type.Namespace.StartsWith("Assets")
                    List<string> fieldJsons = new List<string>();
                    List<string> methodJsons = new List<string>();

                    // load fields
                    FieldInfo[] fields = type.GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
                    foreach (var field in fields) {
                        // skip inherited fields
                        if (field.DeclaringType != type) {
                            continue;
                        }

                        propertyMap.Add("name", field.Name);
                        propertyMap.Add("isStatic", field.IsStatic ? "true" : "false");
                        propertyMap.Add("visibility", field.IsPublic ? "public" : field.IsPrivate ? "private" : "");
                        propertyMap.Add("type", field.FieldType.FullName);
                        fieldJsons.Add(Util.MapToJson(propertyMap));
                        propertyMap.Clear();
                    }

                    // load methods
                    MethodInfo[] methods;
                    try {
                        methods = type.GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
                    } catch (TypeLoadException e) {
                        Logger.Log("[TypeLoadException: " + e.Message + "] Failed to load methods for " + type.FullName + ", skipping.");
                        continue;
                    }
                    foreach (var method in methods) {
                        // skip inherited methods
                        try {
                            if (method.DeclaringType != type && !method.DeclaringType.IsInterface) {
                                continue;
                            }
                        } catch (BadImageFormatException e) {
                            Logger.Log("[TypeLoadException: " + e.Message + "] Failed to load declaring type of method for " + type.FullName + ", skipping.");
                            continue;
                        }

                        ParameterInfo[] parameters = method.GetParameters();
                        List<string> paramJsons = new List<string>();
                        foreach (var parameter in parameters) {
                            propertyMap.Add("name", parameter.Name);
                            propertyMap.Add("type", parameter.ParameterType.FullName);
                            propertyMap.Add("isOut", parameter.IsOut ? "true" : "false");
                            paramJsons.Add(Util.MapToJson(propertyMap));
                            propertyMap.Clear();
                        }

                        propertyMap.Add("name", method.Name);
                        propertyMap.Add("isStatic", method.IsStatic ? "true" : "false");
                        propertyMap.Add("visibility", method.IsPublic ? "public" : method.IsPrivate ? "private" : "");
                        propertyMap.Add("returnType", method.ReturnType.FullName);
                        propertyMap.Add("parameters", Util.ListToJson(paramJsons));
                        methodJsons.Add(Util.MapToJson(propertyMap));
                        propertyMap.Clear();
                    }

                    propertyMap.Add("name", type.Name);
                    propertyMap.Add("namespace", type.Namespace ?? "");
                    propertyMap.Add("visibility", type.IsPublic ? "public" : type.IsNestedPrivate ? "private" : "");
                    propertyMap.Add("typeof", (type.IsAbstract ? "abstract " : "") + (type.IsInterface ? "interface" : type.IsEnum ? "enum" : "class"));
                    propertyMap.Add("basetype", type.BaseType.FullName);
                    propertyMap.Add("interfaces", Util.ListToJson(type.GetInterfaces().Select(interfaceType => "\"" + interfaceType.FullName + "\"")));
                    propertyMap.Add("fields", Util.ListToJson(fieldJsons));
                    propertyMap.Add("methods", Util.ListToJson(methodJsons));
                    typeJsons.Add(Util.MapToJson(propertyMap));
                    propertyMap.Clear();
                }
            }

            Logger.Log("Writing JSON...");
            System.IO.File.WriteAllText(@"C:\Users\Arno\Downloads\Classes.json", Util.ListToJson(typeJsons));
            Logger.Log("Assembly Extractor finished.");
        }

    }

    public static class Util {
        public static IEnumerable<Type> GetLoadableTypes(this Assembly assembly) {
            try {
                return assembly.GetTypes();
            }
            catch (ReflectionTypeLoadException e) {
                Logger.Log("Encountered ReflectionTypeLoadException, using its types.");
                return e.Types.Where(t => t != null);
            }
        }

        public static String MapToJson(Dictionary<string, string> map) {
            String json = "{";
            foreach (string key in map.Keys) {
                if (map.TryGetValue(key, out var value)) {
                    if (value == null) {
                        value = "null";
                    } else if (value != "true" && value != "false" && !double.TryParse(value, out _) && !value.StartsWith("[") && !value.StartsWith("{")) {
                        value = "\"" + value + "\"";
                    }
                    json += (json.Equals("{") ? "" : ",") + "\n\t\"" + key + "\": " + value.Replace("\n", "\n\t");
                }
            }
            json += "\n}";
            return json;
        }

        public static String ListToJson(IEnumerable<string> list) {
            return "[ " + String.Join(", ", list.ToArray()) + " ]";
        }
    }
}
