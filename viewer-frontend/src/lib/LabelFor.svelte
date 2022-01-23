<script lang="ts">
    export let for_id: string
    export let text: string
    export let required: boolean = false
</script>

<label for="{for_id}" class="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">
    {#if required}
        <span class="text-red-500 font-bold">*</span>
    {/if}
    {text}
</label>