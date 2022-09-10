package cphack.testkotlin.controller

import cphack.testkotlin.model.Product


class CacheAPI(TimeToLive: Long) {

    private final val TIME_TO_LIVE : Long = TimeToLive * 1000
    private final val CACHE_MAP : HashMap<String, Product> = HashMap()

    init {
        println("Init cache")
        Thread{
            try{
                while (true){
                    Thread.sleep(TIME_TO_LIVE)
                    cleanupCache()
                }
            }catch (e :InterruptedException ){
                e.printStackTrace()
            }
        }.start()
    }

    private fun cleanupCache() {
        println("Cleanup cache data")
        while (CACHE_MAP.isNotEmpty()){
            CACHE_MAP.clear()
        }
    }

    public fun put(key:String ,value:Product , lang:String) {
        val newKey = lang + key
        CACHE_MAP.put(newKey,value)
    }


    public fun get(key:String, lang:String) : Product? {
        val newKey = lang + key
        try{
            return CACHE_MAP.getValue(newKey)
        }catch (e:Exception){
            return null
        }
    }



}