package com.muyeedahmed.exlexp.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientProvider @Inject constructor() {

    companion object {
        const val SUPABASE_URL = "https://vpwkzljngftfuyatqjzi.supabase.co"
        const val SUPABASE_KEY = "sb_publishable_DsezTQetaxTLqNLqrvl4sQ_c8nTnTmC"
    }

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
}
