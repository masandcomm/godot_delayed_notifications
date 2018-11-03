# config.py

def can_build(env, platform):
    return platform == "android"

def configure(env):
    if env["platform"] == "android":
        env.android_add_dependency("implementation 'com.android.support:appcompat-v7:27.0.0'")
        env.android_add_java_dir("android")
        env.android_add_to_manifest("android/AndroidManifestChunk.xml")
        env.android_add_default_config("minSdkVersion 15")
